package br.com.thomsonreuters.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.thomsonreuters.model.Person;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

@RestController
@Transactional
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	private static final String template = "Hello, %s!";
	private static final String PACKAGE_MODEL = "br.com.thomsonreuters.model.";
	private static String tabela = "";
	private final AtomicLong counter = new AtomicLong();	
	private static String database = "";
	private static String hostCouchdb = "http://127.0.0.1:5984/";
	private boolean queryNewModel = true;

	@Autowired
	private CouchDBDocumentCollectionManager managerCouchDB;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	<T> void salvar(T next){
		entityManager.persist(next);
	}

	@GetMapping(value = "/v1/export")
	public <T> String getData(@RequestParam(value="database") String dbDestination, 
								@RequestParam(value="tabela") String tableDestination) 
			throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		/*
		 * Retornando query e transformando em Java
		 */
	
		tableDestination = StringUtils.capitalize(tableDestination.toLowerCase()).trim();
		RestApiController.tabela = tableDestination;
		RestApiController.database = dbDestination.toLowerCase();
		
		String classe = PACKAGE_MODEL + tableDestination;

		Class<?> clazz = Class.forName(classe);
		ObjectMapper mapper = new ObjectMapper();		
		JavaType tipoClass = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		
		List<T> jsonToJava = null;
		StringBuilder saida = new StringBuilder();
		
		List<DocumentEntity> listDocs;
		
		if ( queryNewModel ) {
			DocumentQuery queryExec = select().from( tableDestination).build(); //.where("_id").eq("iron_man").build();
	        listDocs = managerCouchDB.select(queryExec );
		} else {
			String queryExec = "select * from " + tableDestination;
			listDocs = managerCouchDB.query(queryExec );
		}
				
		for (DocumentEntity doc : listDocs) {

			
			Document docRev1 = doc.getDocuments().stream().filter(s->s.getName().contains("_rev")).findFirst().get();
			System.out.println( "===================================");
			System.out.println( docRev1.getName() );
			System.out.println( docRev1.getValue() );
			System.out.println( "===================================");
			
			
			List<Document> docRev = doc.getDocuments().stream().filter(s->s.getName().contains("_rev")).collect(Collectors.toList());

			Object docRevName = docRev.get(0).getName() ;  
			Object docRevValue = docRev.get(0).getValue().get();

			List<Document> docId = doc.getDocuments().stream().filter(s->s.getName().contains("_id")).collect(Collectors.toList());
			Object docIdName = docId.get(0).getName() ;  
			Object docIdValue = docId.get(0).getValue().get();			
			
			List<Document> docEntity = doc.getDocuments().stream().filter(s->s.getName().contains(RestApiController.tabela)).collect(Collectors.toList());
			Object docName = docEntity.get(0).getName();
			Object docValue = docEntity.get(0).getValue().get();

			logger.info("Tabela["+docName+"]: "+ docIdName +": " +docIdValue);
			logger.info("Tabela["+docName+"]: "+ docRevName +": " +docRevValue);
			logger.info("Tabela["+docName+"]: "+ docValue);
			
			jsonToJava = mapper.readValue( docValue.toString(), tipoClass);			
			Iterator<T> it = jsonToJava.iterator();
			while ( it.hasNext() ) {
				T next = it.next();
				salvar(next);
				saida.append( next + System.lineSeparator());
			}
			saida.append ( "Code: " + removeDoc(docIdValue.toString(), docRevValue.toString()) );
		}
		
		return saida.toString() ;
	}
	
	 String removeDoc(String docIdValue, String docRevValue) throws IOException {
			
		 	String dest = hostCouchdb + database+"/"+docIdValue+"?rev="+docRevValue;
		 	System.out.println(dest);
		 	
			URL url = new URL (dest);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

	        OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
	        osw.write("");
	        osw.flush();
	        osw.close();
	        	        
	        System.err.println(con.getResponseCode() + " - " + con.getResponseMessage());
			
	        return String.valueOf( con.getResponseCode() + " - " + con.getResponseMessage() );
		
	}

	@PostMapping(value = "/v1/import", headers = "Content-Type=application/json")
	public String postImport(@RequestParam(value="tabela") String tableDestination, @RequestBody String jsonData) {
		
		tableDestination = StringUtils.capitalize(tableDestination.toLowerCase());
		
		DocumentEntity documentEntity = DocumentEntity.of(tableDestination);
		documentEntity.add(Document.of("_id", tableDestination + "_" + UUID.randomUUID().toString()));
		documentEntity.add(Document.of( tableDestination, jsonData ));
		DocumentEntity result = managerCouchDB.insert(documentEntity);
		
		String saida = "Document ID: " + result.find("_id").get().getValue().get() ;
		logger.info(saida);

		return saida;
	}
	
	@PutMapping(value = "/v2/import", headers = "Content-Type=application/json")
	public String postImportToNoSql(@RequestParam(value="db") String dbDestination, 
									@RequestParam(value="tabela") String tableDestination, @RequestBody String jsonData) 
							throws IOException {
		tableDestination = StringUtils.capitalize(tableDestination.toLowerCase());
		
		URL url = new URL (hostCouchdb + dbDestination+"/"+tableDestination+"_"+UUID.randomUUID().toString());
		System.out.println(url);
		
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod("PUT");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		jsonData = mapper.writeValueAsString(jsonData);
		
        StringBuilder jsonInputString = new StringBuilder();
        jsonInputString.append("{");
        jsonInputString.append("\""+tableDestination + "\":");
        jsonInputString.append(jsonData+",");
        jsonInputString.append("\"@entity\":"+ "\""+tableDestination +"\"");
        jsonInputString.append("}");		
        
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = jsonInputString.toString().getBytes("utf-8");
		    os.write(input, 0, input.length);           
		}
	
        String codOperation = "Interface return Code : " +  con.getResponseCode() + " - " + con.getResponseMessage();
        logger.info(codOperation);
        System.err.println( codOperation );        
        
        return  codOperation;
		
	}
	
	
	@GetMapping(value = "/v1/person")
    public Person person(@RequestParam(value="name", defaultValue="World") String name) {
        return new Person(counter.incrementAndGet(), String.format(template, name));        
    }
	
	@GetMapping(value = "/v1/hello")
	public String helloWorld() {
		return "Hello !";
	}
}
