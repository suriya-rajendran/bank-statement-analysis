package com.bankstatement.perfios.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bankstatement.perfios.configuration.PerfiosConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BsaPerfiosHelper {

	private static final Logger logger = LoggerFactory.getLogger(BsaPerfiosHelper.class);

	public static final String SIGNED_HEADER_NAMES = "host;x-perfios-content-sha256;x-perfios-date\n";

	public static final String PERFIOS_RSA_SHA_256 = "PERFIOS-RSA-SHA256";

	public static final String X_PERFIOS_DATE = "X-Perfios-Date";

	public static final String X_PERFIOS_CONTENT_SHA_256 = "X-Perfios-Content-Sha256";

	public static final String HOST = "host";

	private static final SimpleDateFormat PERFIOS_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	@Autowired
	PerfiosConfiguration perfiosConfiguration;

	protected PrivateKey privateKey;

	ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void onLoad() {
		if (!StringUtils.isEmpty(perfiosConfiguration.getPrivateKeyPath()))

			try {
				byte[] encoded = Files.readAllBytes(Paths.get(perfiosConfiguration.getPrivateKeyPath()));
				String privateKeyPem = new String(encoded);
				PEMReader pemReader = new PEMReader(new StringReader(privateKeyPem));
				KeyPair keyPair = (KeyPair) pemReader.readObject();
				privateKey = keyPair.getPrivate();
			} catch (IOException e) {
				logger.error("Error while loading key pair", e);
			}
	}

	public String makeDigest(String payload, String digestAlgo) {
		String strDigest = "";
		try {
			MessageDigest md = MessageDigest.getInstance(digestAlgo);
			md.update(payload.getBytes("UTF-8"));
			byte[] digest = md.digest();
			byte[] encoded = Hex.encode(digest);
			strDigest = new String(encoded);
		} catch (Exception ex) {
			logger.error("Error make digest ", ex);
			ex.printStackTrace();
		}
		return strDigest;
	}

	public String getSignature(String signatureAlgo, PrivateKey privateKey, String digestAlgo, String payload) {
		String digest = makeDigest(payload, digestAlgo);
		return encrypt(digest, signatureAlgo, privateKey);
	}

	private String encrypt(String digest, String signatureAlgo, PrivateKey privateKey) {
		String strEncrypted = "";
		try {
			Cipher cipher = Cipher.getInstance(signatureAlgo);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] encrypted = cipher.doFinal(digest.getBytes("UTF-8"));
			byte[] encoded = Hex.encode(encrypted);
			strEncrypted = new String(encoded);
		} catch (Exception ex) {
			logger.error("Error while encrypt ", ex);
			ex.printStackTrace();
		}
		return strEncrypted;
	}

	public String createSignatureString(String canonicalRequest, String date) {
		StringBuilder sb = new StringBuilder(PERFIOS_RSA_SHA_256 + "\n");
		sb.append(date).append("\n").append(makeDigest(canonicalRequest, perfiosConfiguration.getDigestAlgorithm()));
		return makeDigest(sb.toString(), perfiosConfiguration.getDigestAlgorithm());
	}

	public Map<String, Object> createCanonicalHeaders(String payloadForSignature) {
		final LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
		headers.put(HOST, perfiosConfiguration.getHost().toLowerCase());
		headers.put(X_PERFIOS_DATE, formatDate(new Date()));
		return headers;
	}

	public String createCanonicalRequest(String method, String uri, String payload, Map<String, Object> params,
			Map<String, Object> headers) {
		StringBuilder sb = new StringBuilder();
		sb.append(method).append("\n");
		sb.append(uri).append("\n");
		final String canonicalParams = createCanonicalEntries(params, this::encodeValue, this::encodeValue, "=", "&");
		sb.append(canonicalParams).append(canonicalParams.trim().isEmpty() ? "\n" : "\n");
		final String canonicalHeaders = createCanonicalEntries(headers, this::formatHeaderKey, this::formatHeaderValue,
				":", "\n");
		sb.append(canonicalHeaders).append(canonicalHeaders.trim().isEmpty() ? "\n" : "\n");
		sb.append(SIGNED_HEADER_NAMES);
		if (payload == null) {
			payload = "";
		}
		sb.append(makeDigest(payload, perfiosConfiguration.getDigestAlgorithm()));
		return sb.toString();
	}

	private String formatHeaderKey(String val) {
		if (val != null) {
			return val.toLowerCase();
		}
		return null;
	}

	private String formatHeaderValue(String val) {
		if (val != null) {
			return val.trim();
		}
		return null;
	}

	private String encodeValue(String uriComponent) {
		try {
			return URLEncoder.encode(uriComponent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Error encoding value - %s", uriComponent), e);
		}
		return uriComponent;
	}

	public String createCanonicalEntries(Map<String, Object> params, Function<String, String> keyFormatter,
			Function<String, String> valueFormatter, String operator, String delimiter) {
		List<String> paramsList = new ArrayList<>();
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				paramsList.add(encodeEntry(entry, keyFormatter, valueFormatter, operator, delimiter));
			}
		}
		return String.join(delimiter, paramsList);
	}

	private String encodeEntry(Map.Entry<String, Object> entry, Function<String, String> keyFormatter,
			Function<String, String> valueFormatter, String operator, String delimiter) {
		if (entry != null) {
			if (entry.getValue() instanceof String) {
				return formatCanonicalEntry(entry.getKey(), (String) entry.getValue(), keyFormatter, valueFormatter,
						operator);
			} else if (entry.getValue() instanceof Collection) {
				return ((Collection<?>) entry.getValue()).stream().map(
						e -> formatCanonicalEntry(entry.getKey(), e.toString(), keyFormatter, valueFormatter, operator))
						.collect(Collectors.joining(delimiter));
			}
		}
		return null;
	}

	private String formatCanonicalEntry(String key, String value, Function<String, String> keyFormatter,
			Function<String, String> valueFormatter, String operator) {
		String entryKey = keyFormatter != null ? keyFormatter.apply(key) : key;
		String entryValue = valueFormatter != null ? valueFormatter.apply(value) : value;
		return String.format("%s%s%s", entryKey, operator, entryValue);
	}

	public String formatDate(Date date) {
		return PERFIOS_DATE_FORMAT.format(date);
	}

	// --------------------------------------------------------------------------------------//---------------------------------------------------------

	public <T extends HttpRequestBase> HttpResponse executeRequest(Class<T> method, String uri, String payload,
			String contentType, Map<String, Object> params, Map<String, Object> headers)
			throws URISyntaxException, UnsupportedEncodingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		HttpRequestBase request = createHttpMethod(method, uri, params);
		if (request != null) {
			String payloadForSignature = payload != null ? payload : "";
			String signature = getSignature(perfiosConfiguration.getSignatureAlgorithm(), privateKey,
					perfiosConfiguration.getDigestAlgorithm(), payloadForSignature);
			logger.debug("signature - {}", signature);
			logger.info("Create signature completed ");
			request.addHeader("Content-Type", contentType);
			request.addHeader("Host", perfiosConfiguration.getHost());
			setHttpHeaders(request, headers);
			String entity = getHttpEntity(payload, signature);
			HttpClient client = HttpClients.createDefault();
			try {
				if (request instanceof HttpPost && entity != null) {
					((HttpPost) request).setEntity(new StringEntity(entity, "UTF-8"));
				}
				HttpResponse response = client.execute(request);
				final String responseBody = EntityUtils.toString(response.getEntity());
				response.setEntity(new StringEntity(responseBody));
				logger.debug("Status - {}, Body - {} ", response.getStatusLine().getStatusCode(), responseBody);
				return response;
			} catch (IOException e) {
				logger.error("Error invoking perfios service", e);
			}
		}
		return null;
	}

	private String getHttpEntity(String payload, String signature) throws UnsupportedEncodingException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("payload", payload);
		parameters.put("signature", signature);
		StringBuilder result = new StringBuilder();
		boolean firstEntry = true;
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (firstEntry)
				firstEntry = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return result.toString();
	}

	private HttpRequestBase createHttpMethod(Class<? extends HttpRequestBase> method, String uri,
			Map<String, Object> params) throws URISyntaxException {
		HttpRequestBase pMethod = null;
		final URI requestUri = prepareHttpParams(uri, params);
		if (HttpPost.class.equals(method)) {
			pMethod = new HttpPost(requestUri);
		} else if (HttpGet.class.equals(method)) {
			pMethod = new HttpGet();
		}
		return pMethod;
	}

	private void setHttpHeaders(HttpRequestBase pMethod, Map<String, Object> headers) {
		if (headers == null) {
			return;
		}
		for (Map.Entry<String, Object> header : headers.entrySet()) {
			if (header.getValue() instanceof String) {
				pMethod.addHeader(header.getKey(), (String) header.getValue());
			} else if (header.getValue() instanceof Collection) {
				for (Object h : ((Collection<?>) header.getValue())) {
					pMethod.addHeader(header.getKey(), h.toString());
				}
			}
		}
	}

	private URI prepareHttpParams(String uri, Map<String, Object> params) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme("https");
		uriBuilder.setHost(perfiosConfiguration.getHost());
		uriBuilder.setPath(perfiosConfiguration.getBaseUrl() + uri);
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof Collection) {
					for (String val : ((Collection<String>) entry.getValue())) {
						uriBuilder.setParameter(entry.getKey(), val);
					}
				} else {
					uriBuilder.setParameter(entry.getKey(), (String) entry.getValue());
				}
			}
		}
		return uriBuilder.build();
	}

}