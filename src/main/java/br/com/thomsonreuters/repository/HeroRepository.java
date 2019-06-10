package br.com.thomsonreuters.repository;

import java.util.List;
import java.util.Optional;

import org.jnosql.artemis.Repository;

import br.com.thomsonreuters.model.Hero;

public interface HeroRepository extends Repository<Hero, String> {

    Optional<Hero> findByName(String name);

    List<Hero> findByAgeGreaterThan(Integer age);

    List<Hero> findByAgeLessThan(Integer age);

    void deleteByName(String name);

}