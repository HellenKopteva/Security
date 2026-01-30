package org.example.Security;

import org.example.Security.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {

    boolean existsByLogin(String login);

    Optional<Person> findPersonByLogin(String login);
}
