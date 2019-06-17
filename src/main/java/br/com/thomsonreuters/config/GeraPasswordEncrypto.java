package br.com.thomsonreuters.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeraPasswordEncrypto {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}
}
