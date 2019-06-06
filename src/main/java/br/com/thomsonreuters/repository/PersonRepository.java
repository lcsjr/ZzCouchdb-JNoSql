package br.com.thomsonreuters.repository;

import org.jnosql.artemis.Repository;

import br.com.thomsonreuters.model.Person;

public interface PersonRepository extends Repository<Person, Long> {

}
