package br.com.thomsonreuters.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"br.com.thomsonreuters"})
public class ZzCouchdbJNoSqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZzCouchdbJNoSqlApplication.class, args);
	}

}
