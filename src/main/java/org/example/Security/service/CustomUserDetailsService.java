package org.example.Security.service;

import org.example.Security.models.Person;
import org.example.Security.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public boolean userExists(String username) {
        return personRepository.existsByLogin(username);
    }
    @Override
    public UserDetails loadUserByUsername(String login) {
        Person user = personRepository.findPersonByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));

        System.out.println(user.toString());
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole()))// Добавляем префикс ROLE_ (стандарт Spring Security)
                .build();
    }
}