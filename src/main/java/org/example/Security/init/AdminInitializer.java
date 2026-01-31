package org.example.Security.init;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.Security.entity.Person;
import org.example.Security.repository.PersonRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@DependsOn("entityManagerFactory")
public class AdminInitializer {

    private final PersonRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void createAdmin() {
        if (!repository.existsByLogin("admin")) {
            repository.save(
                    new Person(
                            "admin",
                            passwordEncoder.encode("admin123"),
                            "admin@local.dev",
                            "ROLE_ADMIN"
                    )
            );
            System.out.println("✔ ADMIN CREATED");
        } else {
            System.out.println("ℹ ADMIN EXISTS");
        }
    }
}
