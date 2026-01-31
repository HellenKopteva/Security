package org.example.Security.service.security;

import org.example.Security.entity.Person;
import org.example.Security.repository.PersonRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository repository;

    public CustomUserDetailsService(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Person person = repository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));

        return User.builder()
                .username(person.getLogin())
                .password(person.getPassword())
                .authorities(person.getRole())
                .build();
    }
}
