package br.com.thomsonreuters.model;

import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity("Person")
public class Person {

	@Id("id")
	private long id;
	@Column
	private String name;

}
