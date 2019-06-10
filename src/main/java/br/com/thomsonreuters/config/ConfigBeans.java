package br.com.thomsonreuters.config;

import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManagerFactory;
import org.jnosql.diana.couchdb.document.CouchDBDocumentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeans {
	
	@Bean
	public CouchDBDocumentCollectionManager managerCouchDB() {
		CouchDBDocumentConfiguration config = new CouchDBDocumentConfiguration();
		CouchDBDocumentCollectionManagerFactory managerFactory = config.get();
		CouchDBDocumentCollectionManager managerCouchDB = managerFactory.get("my-db");

		return managerCouchDB;
	}

//	@Bean
//	public AnnotationConfigApplicationContext context() {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.scan(CouchDbJNoSqlApplication.class.getPackage().getName());
//		context.register(ConfigBeans.class);
//		context.refresh();
//		return context;
//	}

}
