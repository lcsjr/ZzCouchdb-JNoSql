package br.com.thomsonreuters.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentPreparedStatement;
import org.jnosql.diana.couchdb.document.CouchDBDocumentCollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
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

import br.com.thomsonreuters.model.Person;

@RestController
@RequestMapping("/api")
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();	

	@Autowired
	private CouchDBDocumentCollectionManager managerCouchDB;
//	@Autowired
//	private AnnotationConfigApplicationContext context;
//	@Autowired
//	private HeroRepository heroRepo;
	
	@RequestMapping(value = "/import/{type}", headers = "Content-Type=application/json", method = RequestMethod.POST)
	public <T> String getAcesse(@PathVariable String type, @RequestBody String data)
			throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
		

//		System.out.println(context.getBeanDefinitionCount() );
//		System.out.println(context.getBeanDefinitionNames() );
//		
//        for (String beanName : context.getBeanDefinitionNames()) {
//            System.out.println(beanName);
//        }
		
		
		type = StringUtils.capitalize(type);
		String classe = "br.com.thomsonreuters.model." + type;
		Class<?> clazz = Class.forName(classe);
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType tipoClass = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List<T> jsonToJava = mapper.readValue(data, tipoClass);
		
		System.out.println("Obj Factory: " );
		jsonToJava.stream().forEach(System.out::println);
		System.out.println();
		
		DocumentEntity documentEntity = DocumentEntity.of(type);
		documentEntity.add(Document.of("_id", type + "_" + UUID.randomUUID().toString()));
		documentEntity.add(Document.of( type, jsonToJava.toString() ));
		managerCouchDB.insert(documentEntity);
		
		
		DocumentPreparedStatement prepare = managerCouchDB.prepare("select * from Hero");
		List<DocumentEntity> heros = prepare.getResultList();
		
		for (DocumentEntity hr : heros) {

			List<Document> hs = hr.getDocuments().stream()
					.filter(s->s.getName().contains("Hero"))
					.collect(Collectors.toList());

			System.out.println(" ======= 1");
			hs.forEach(System.out::println);
			System.out.println(" ======= 2");
			System.out.println(hs.toString());
			System.out.println(" ======= 3");
			
			List <T> jsonToJava2 = mapper.readValue(  hs.toString(), tipoClass);
			System.out.println(" ======= 4");
			jsonToJava2.stream().forEach(System.out::println);
			System.out.println(" ======= 5");
			
		}		

		
//		
//		System.out.println( "------------------------------------------------" );
//		heros.forEach(System.out::println);
//		System.out.println( "------------------------------------------------" );
//		
//		JavaType tipoClass2 = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
//		
//		
//		
//		
//		List<T> jsonToJava2 = mapper.readValue(heros.toString(), tipoClass2);
//		System.out.println(jsonToJava2);
		
		
//		DocumentTemplate template = context.select(DocumentTemplate.class).get();
//		template.insert(ironMan);
		
		
		
		
//	    ClienteServico servico = context.getBean(ClienteServico.class);
//	    servico.salvar(new Cliente(1, "Aluno João da Silva"));
	    
//	    Hero heroi = context.getBean(Hero.class);
//	    servico.salvar(new Cliente(1, "Aluno João da Silva"));
		
		
		
//        Hero ironMan = Hero.builder()
//				.withRealName("Tony Stark").withName("iron_man").withAge(34)
//				.build();
//        
//        
//        System.out.println("Objeto: " + ironMan);


//		  Hero ironMan = Hero.builder().withRealName("Tony Stark").withName("iron_man")
//                .withAge(34).build();
//		  
//		  SeContainer container = SeContainerInitializer.newInstance().initialize();
//		  DocumentTemplate template = container.select(DocumentTemplate.class).get();
//		  template.insert(ironMan);
//		  
//        Hero ironMan = Hero.builder().withRealName("Tony Stark").withName("iron_man")
//                .withAge(34).withPowers(Collections.singleton("rich")).build();
//        DocumentTemplate template = container.select(DocumentTemplate.class).get();
//
//        template.insert(ironMan);
//
//        DocumentQuery query = select().from("Hero").where(eq(Document.of("_id", "iron_man"))).build();
//        List<Hero> heroes = template.select(query);
//        System.out.println(heroes);

		
		
		
		
//		DocumentQuery documentQuery = new CouchDBDocumentQuery(documentQuery );
		
		
//		List<DocumentEntity> query = managerCouchDB.query("select * from " + type);
//		
//		System.out.println("============================");
//		System.out.println("aqui com Factory......" + jsonToJava);//
//
//		for (DocumentEntity hr : query) {
//
//			List<Document> hs = hr.getDocuments().stream()
//					.filter(s->s.getName().contains("Hero"))
//					.collect(Collectors.toList());
//
//			hs.forEach(System.out::println);
//
//			System.out.println(hs.parallelStream().findFirst());
			
//			List <T> jsonToJava2 = mapper.readValue(  hs, tipoClass);
//			jsonToJava2.stream().forEach(System.out::println);			
//			
//		}

//		for (Hero hero : jsonToJava) {
//			System.out.println(hero.toString());
//		}

		System.out.println("============================");

//		DocumentQuery entity  ;
//		CouchDBDocumentQuery quey = CouchDBDocumentQuery.of(null, "books");
//		List<DocumentEntity> documentsFound = collectionManager.find(query);

//        DocumentQuery query = DocumentQuery.of(COLLECTION_NAME);
//        query.and(DocumentCondition.eq(id.get()));
//        List<DocumentEntity> documentsFound = collectionManager.find(query);

//		entityManager = managerFactory.get("people");
//		Object id = entity.find(ID).map(Document::get).get();
//        DocumentQuery query = select().from(COLLECTION_NAME).where(ID).eq(id).build();
//        DocumentEntity documentFound = entityManager.singleResult(query).get();
//        
//		MangoQueryConverter converter = new MangoQueryConverter();

//		DocumentQuery query = select().from("books").where("_id").eq(100).build();
//		List<DocumentEntity> entities = manager.select(query)

//		log.info("============================="+managerFactory);

		return "oi";// heroRepository..findAll();
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
