package org.example.Security;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.http.ResponseEntity.status;

@Service
public class PersonApiService implements PersonApiInterface {
    private static final Pattern TITLE_PATTERN = Pattern.compile("^\\s*[a-zA-Zа-яА-ЯёЁ'][a-zA-Zа-яА-ЯёЁ' -]*[a-zA-Zа-яА-ЯёЁ']?[.!?,;:]?\\s*$");//разрешает предложения только из слов и пробелов и знаков препинания
    private final PersonRepository personRepository;
    private final Faker faker;

    @Autowired
    public PersonApiService(PersonRepository personRepository, Faker faker) {
        this.personRepository = personRepository;
        this.faker = faker;
    }

    @Override
    public ResponseEntity<PersonDTO> createUser(UserDetails userDetails, boolean isAdmin) {
        generationData();
        Person person=new Person(userDetails.getUsername(), userDetails.getPassword(), faker.internet().emailAddress(),isAdmin?"ADMIN":"USER",LocalDateTime.now());
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
        generationData();
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
        generationData();
        if (personRepository.count() > 0) {
            personRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }


    //----------------------------------------------------------
    public ResponseEntity<Long> getTotalCount() {
        generationData();
        try {
            return ResponseEntity.ok(personRepository.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Override
    public ResponseEntity<Boolean> existsById(Long id) {
        generationData();
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
            for (int i = 0; i < 100; i++) {
                String login = faker.name().username();
                String password=faker.pokemon().name();
                String domain=faker.internet().emailAddress();
                String role=random.nextBoolean()?"USER":"ADMIN";
                LocalDateTime registrationDate=LocalDateTime.now();
                Person person=new Person(login,password,domain,role,registrationDate);
                personRepository.save(person);
            }
        }
    }
}