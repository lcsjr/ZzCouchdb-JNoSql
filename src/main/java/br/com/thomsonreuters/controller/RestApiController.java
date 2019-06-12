package br.com.thomsonreuters.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.thomsonreuters.model.Hero;
import br.com.thomsonreuters.model.Person;

@RestController
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	private static final String template = "Hello, %s!";
	private static final String packageModel = "br.com.thomsonreuters.model.";
	private static String tabela = "";
	private final AtomicLong counter = new AtomicLong();	

	@Autowired
	private CouchDBDocumentCollectionManager managerCouchDB;

	@GetMapping(value = "/export")
	public <T> String getData(@RequestParam(value="tabela") String tableDestination) 
			throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		/*
		 * Retornando query e transformando em Java
		 */
		tableDestination = StringUtils.capitalize(tableDestination.toLowerCase()).trim();
		RestApiController.tabela = tableDestination;
		String classe = packageModel + tableDestination;

		Class<?> clazz = Class.forName(classe);
		ObjectMapper mapper = new ObjectMapper();
		JavaType tipoClass = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		
		List<T> jsonToJava = null;
		StringBuilder saida = new StringBuilder();
		String queryExec = "select * from " + tableDestination;

		List<DocumentEntity> query = managerCouchDB.query(queryExec );
		
		for (DocumentEntity hr : query) {

			List<Document> hs = hr.getDocuments().stream()
					.filter(s->s.getName().contains(RestApiController.tabela))
					.collect(Collectors.toList());

			Object docValue = hs.get(0).getValue().get();
			logger.info("Tabela["+RestApiController.tabela+"]: "+ docValue);
			
			jsonToJava = mapper.readValue( docValue.toString(), tipoClass);
			
			Iterator<T> it = jsonToJava.iterator();
			while ( it.hasNext() ) {
				Hero next = (Hero) it.next();
				saida.append("Id -> " +next.getId() + ";");
				saida.append("Name -> " +next.getName() + ";");
				saida.append("Real Name -> " +next.getRealName() + ";");
				saida.append("Age -> " +next.getAge() +";" + System.lineSeparator());
				
				System.out.println( saida );
			}
						
		}		
		return saida.toString() ;
	}
	
//	@PostMapping(value = "/import/{tableDestino}", headers = "Content-Type=application/json")
	@PostMapping(value = "/import", headers = "Content-Type=application/json")
	public String postImport(@RequestParam(value="tabela") String tableDestination, @RequestBody String jsonData) {
		
		tableDestination = StringUtils.capitalize(tableDestination.toLowerCase());
		
		DocumentEntity documentEntity = DocumentEntity.of(tableDestination);
		documentEntity.add(Document.of("_id", tableDestination + "_" + UUID.randomUUID().toString()));
		documentEntity.add(Document.of( tableDestination, jsonData ));
		DocumentEntity result = managerCouchDB.insert(documentEntity);
		Object numProtocol = result.find("_id").get().getValue().get();
		
		String saida = "Protocolo de importacao: "+numProtocol ;
		logger.info(saida);

		return saida;
	}
	
	@GetMapping(value = "/person")
    public Person person(@RequestParam(value="name", defaultValue="World") String name) {
        return new Person(counter.incrementAndGet(), String.format(template, name));        
    }
	
	@GetMapping(value = "/hello")
	public String helloWorld() {
		return "Hello !";
	}
}
