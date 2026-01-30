package org.example.Security.service;

import com.github.javafaker.Faker;
import org.example.Security.models.Person;
import org.example.Security.models.PersonDTO;
import org.example.Security.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class PersonApiService implements PersonApiInterface {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;

    @Autowired
    public PersonApiService(PersonRepository personRepository, PasswordEncoder passwordEncoder, Faker faker) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.faker = faker;
        generationData();
        System.out.println(personRepository.count()>0?"пользователи в бд появились или были":"БД пуста");
    }

    @Override
    public ResponseEntity<PersonDTO> createUser(UserDetails userDetails, boolean isAdmin) {
        Person person=new Person(userDetails.getUsername(), userDetails.getPassword(), "abracadabra@gmail.com",isAdmin?"ADMIN":"USER",LocalDateTime.now());
        if (personRepository.existsByLogin(person.getLogin())) return ResponseEntity.badRequest().build();
        try {
            person = personRepository.save(person);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()// Берет базовый URL: http://localhost
                    .path("/api/home/getById")// Добавляет путь → /api/home/getById
                    .queryParam("id", person.getId())
                    .build()
                    .toUri();
            return ResponseEntity.created(location).body(person.getDTO());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

        //---------------------------------------------------------------
    @Override
    public ResponseEntity<Void> deleteById(Long id) {
        if (id < 1) return ResponseEntity.badRequest().build();
        try {
            if (personRepository.existsById(id)) {
                personRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAll() {
        if (personRepository.count() > 0) {
            personRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }


    //----------------------------------------------------------
    public ResponseEntity<Long> getTotalCount() {
        try {
            return ResponseEntity.ok(personRepository.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Boolean> existsById(Long id) {
        if (id < 1) return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(personRepository.existsById(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    public List<PersonDTO> personToDtoList(List<Person> list) {
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person person : list) {
            personDTOList.add(person.getDTO());
        }
        return personDTOList;
    }

    public void generationData(){
        Random random = new Random();
        if(personRepository.count()==0){
            if (!personRepository.existsByLogin("daniil")) {
                personRepository.save(new Person("daniil",passwordEncoder.encode("daniil123"),"qvaqva@gmail.com","USER",LocalDateTime.now()));
            }
            if (!personRepository.existsByLogin("elena")) {
                personRepository.save(new Person("elena",passwordEncoder.encode("elena123"),"qvaqva1@gmail.com","ADMIN",LocalDateTime.now()));
            }
            if (!personRepository.existsByLogin("mikhail")) {
                personRepository.save(new Person("mikhail",passwordEncoder.encode("mikhail123"),"qvaqva2@gmail.com","USER",LocalDateTime.now()));
            }
            if (!personRepository.existsByLogin("kittony")) {
                personRepository.save(new Person("kittony",passwordEncoder.encode("kittony123"),"qvaqva3@gmail.com","ADMIN",LocalDateTime.now()));
            }
            for (int i = 0; i < 10; i++) {
                String login = faker.name().username();
                String password=passwordEncoder.encode(faker.pokemon().name());
                String domain=faker.internet().safeEmailAddress();
                String role=random.nextBoolean()?"USER":"ADMIN";
                LocalDateTime registrationDate=LocalDateTime.now();
                Person person=new Person(login,password,domain,role,registrationDate);
                personRepository.save(person);
            }
        }
    }
}