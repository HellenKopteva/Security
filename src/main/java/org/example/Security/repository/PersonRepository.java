package org.example.Security.repository;

import org.example.Security.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByLogin(String login);

    boolean existsByLogin(String login);
}
