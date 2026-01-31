package org.example.Security.service.user;

import org.example.Security.dto.PersonDto;
import org.example.Security.entity.Person;
import org.example.Security.repository.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public PersonDto createUser(String login, String rawPassword, String email, boolean admin) {
        if (repository.existsByLogin(login)) {
            throw new IllegalArgumentException("User already exists");
        }

        Person person = new Person(
                login,
                passwordEncoder.encode(rawPassword),
                email,
                admin ? "ROLE_ADMIN" : "ROLE_USER"
        );

        repository.save(person);
        return toDto(person);
    }

    private PersonDto toDto(Person person) {
        return new PersonDto(
                person.getLogin(),
                person.getEmail(),
                person.getRole(),
                person.getRegistrationDate()
        );
    }
}
