package br.com.thomsonreuters.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.thomsonreuters.model.Hero;

@Repository
public interface HeroRepository extends JpaRepository<Hero, String> {

    Optional<Hero> findByName(String name);

    List<Hero> findByAgeGreaterThan(Integer age);

    List<Hero> findByAgeLessThan(Integer age);

    void deleteByName(String name);

}