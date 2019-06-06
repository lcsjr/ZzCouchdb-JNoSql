package br.com.thomsonreuters.controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.thomsonreuters.model.Person;

@Controller
@RequestMapping(value = "/api")
public class CouchDBController {

	private static final Logger logger = LoggerFactory.getLogger(CouchDBController.class);
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
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
		
	@RequestMapping(value = "/import/{type}", headers="Content-Type=application/json", method = RequestMethod.POST)
	@ResponseBody	
	public String quote(@PathVariable String type, @RequestBody String data) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException  {
		
		String classe = "br.com.thomsonreuters.model."+ type;
		Class<?> clazz = Class.forName(classe);
		
		logger.info("============================================= " + clazz.toString());
		ObjectMapper objectMapper = new ObjectMapper();	
		Object jsonToJava = objectMapper.readValue(data, clazz  );
		
		
		logger.info("============================================= " + jsonToJava.toString());
		
		if (jsonToJava instanceof Person) {
			
		}
		

	    return jsonToJava.toString();
	}	
}
