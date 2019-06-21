package br.com.thomsonreuters.config;

import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManagerFactory;
import org.jnosql.diana.couchdb.document.CouchDBDocumentConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("br.com.thomsonreuters.model")
@EnableJpaRepositories("br.com.thomsonreuters.model.repository")
public class ConfigBeans {

	@Bean
	public CouchDBDocumentCollectionManager managerCouchDB() {
		CouchDBDocumentConfiguration config = new CouchDBDocumentConfiguration();		
		CouchDBDocumentCollectionManagerFactory managerFactory = config.get();
		CouchDBDocumentCollectionManager managerCouchDB = managerFactory.get("my-db");
		
		return managerCouchDB;
	}
	
}
