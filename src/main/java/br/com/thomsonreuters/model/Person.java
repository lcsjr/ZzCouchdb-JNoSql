package br.com.thomsonreuters.model;

import org.jnosql.artemis.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {

	private long id;
	private String name;

}
