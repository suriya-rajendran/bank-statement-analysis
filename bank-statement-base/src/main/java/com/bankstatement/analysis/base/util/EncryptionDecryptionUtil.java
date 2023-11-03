package com.bankstatement.analysis.base.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionDecryptionUtil {

	private static String secretKey;

	private static String salt;

	@Autowired
	public EncryptionDecryptionUtil(@Value("${security.secretKey}") String secretKey,
			@Value("${security.salt}") String salt) {
		EncryptionDecryptionUtil.secretKey = secretKey;
		EncryptionDecryptionUtil.salt = salt;
	}

	public static String encryptList(List<String> stringList) {
		String concatenatedStrings = String.join(",", stringList);
		return Encryptors.text(secretKey, salt).encrypt(concatenatedStrings); 
	}

	public static List<String> decryptString(String encryptedString) {
		TextEncryptor encryptor = Encryptors.text(secretKey, salt);
		String decryptedString = encryptor.decrypt(encryptedString);
		return Arrays.asList(decryptedString.split(","));
	}
}
