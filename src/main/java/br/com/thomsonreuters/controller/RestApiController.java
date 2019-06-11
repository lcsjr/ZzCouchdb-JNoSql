package br.com.thomsonreuters.controller;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
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
	private static String tabela = "";
	private final AtomicLong counter = new AtomicLong();	

	@Autowired
	private CouchDBDocumentCollectionManager managerCouchDB;

	
	@PostMapping(value = "/import/{tableDestino}", headers = "Content-Type=application/json")
	public <T> String getAcesse(@PathVariable String tableDestino, @RequestBody String jsonData)
			throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {

		tableDestino = StringUtils.capitalize(tableDestino.toLowerCase());
		RestApiController.tabela = tableDestino;
		
		String classe = "br.com.thomsonreuters.model." + tableDestino;
		Class<?> clazz = Class.forName(classe);
		ObjectMapper mapper = new ObjectMapper();
		JavaType tipoClass = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List<T> jsonToJava = mapper.readValue(jsonData, tipoClass);
		
		System.out.println("Obj Factory: " );
		jsonToJava.stream().forEach(System.out::println);
		System.out.println("______________________________________________");
		
		DocumentEntity documentEntity = DocumentEntity.of(tableDestino);
		documentEntity.add(Document.of("_id", tableDestino + "_" + UUID.randomUUID().toString()));
		documentEntity.add(Document.of( tableDestino, jsonData ));
		managerCouchDB.insert(documentEntity);
		
		
		/*
		 * Retornando query e transformando em Java
		 */
		
		List<DocumentEntity> query = managerCouchDB.query("select * from " + tableDestino);
		
		List<T> jsonToJava2 = null;
		StringBuilder saida = new StringBuilder();
		
		for (DocumentEntity hr : query) {

			List<Document> hs = hr.getDocuments().stream()
					.filter(s->s.getName().contains(RestApiController.tabela))
					.collect(Collectors.toList());

			Object docValue = hs.get(0).getValue().get();
			System.out.println(docValue);
			
			jsonToJava2 = mapper.readValue( docValue.toString(), tipoClass);
			
			Iterator<T> it = jsonToJava2.iterator();
			while ( it.hasNext() ) {
				Hero next = (Hero) it.next();
				saida.append("Id -> " +next.getId() + System.lineSeparator());
				saida.append("Name -> " +next.getName() + System.lineSeparator());
				saida.append("Real Name -> " +next.getRealName() + System.lineSeparator());
				saida.append("Age -> " +next.getAge() + System.lineSeparator() + System.lineSeparator());
				
				System.out.println( saida );
			}
						
		}		


		return saida.toString() ;
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "/person")
	@ResponseBody
    public Person person(@RequestParam(value="name", defaultValue="World") String name) {
        return new Person(counter.incrementAndGet(), String.format(template, name));        
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	@ResponseBody
	public String helloWorld() {
		return "Hello !";
	}
}
