package org.example.Security;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {
    List<Person> findAllByTitleContainsIgnoreCase(String title);
    Person getPersonByTitle(String title);
    Long countPersonByYearBetween(Integer yearAfter, Integer yearBefore);
    Boolean existsByTitle(String title);
    List<Person> findAllByTitleAndYearAfter(String title, Integer yearAfter);
    List<Person> findAllByYearBetween(Integer yearAfter, Integer yearBefore);
    List<Person> findAllByYear(Integer year);

    boolean existsPersonByTitle(String title);
    int deletePersonByTitle(String title);

    boolean existsPersonByYearAfter(Integer yearAfter);
    @Transactional
    int deletePersonByYearAfter(Integer min);

    boolean existsPersonByYearBefore(Integer yearBefore);
    @Transactional
    int deletePersonByYearBefore(@Param("max") Integer max);

    boolean existsPersonByYearBetween(Integer yearAfter, Integer yearBefore);

    @Modifying
    @Query("DELETE FROM Person WHERE year BETWEEN :min AND :max")
    @Transactional
    int deletePersonByYearBetween(@Param("min") Integer min, @Param("max") Integer max);

    @Modifying
    @Query("UPDATE Person u SET u.title = :title WHERE u.id = :id")
    @Transactional
    Person updateTitle(@Param("id") Long id, @Param("title") String title);

    @Modifying
    @Query("UPDATE Person u SET u.year = :year WHERE u.id = :id")
    @Transactional
    Person updateYear(@Param("id") Long id, @Param("year") Integer year);
}
