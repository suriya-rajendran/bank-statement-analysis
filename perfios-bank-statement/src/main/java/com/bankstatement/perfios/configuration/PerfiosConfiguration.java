package com.bankstatement.perfios.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "perfios")
@Configuration
@Data
public class PerfiosConfiguration {

	private String vendorCode = "PFS";

	private String returnUrl;

	private String callBackUrl;

	private String vendor;

	private String initTransactionUrl;

	private String txnStatusUrl;

	private String retrieveReportUrl;

	private String version;

	private String privateKeyPath;

	private String reportFormat;

	private String host = "demo.perfios.com";

	private String signatureAlgorithm = "SHA256withRSA/PSS";

	private String baseUrl = "/KuberaVault/api/v3";

	private String digestAlgorithm = "SHA-256";

	private String downloadReportFormat="pdf";
}
