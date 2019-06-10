package br.com.thomsonreuters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication // (scanBasePackages={"com.couchdb.*.*"})
//@EnableJpaRepositories("com.couchdb.model.DAO.repository.*.*")
//@EntityScan//("com.couchdb.model.*.*")
public class CouchDbJNoSqlApplication {

	public static void main(String[] args) {

		SpringApplication.run(CouchDbJNoSqlApplication.class, args);
//		context.close();

	}



}
