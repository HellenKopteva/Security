package org.example.Security;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Логин может содержать только буквы, цифры, точки, дефисы и подчеркивания")
    @Column(unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 5, message = "Пароль должен содержать минимум 5 символов")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Домен не может быть пустым")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Некорректный формат домена")
    @Column(columnDefinition = "NVARCHAR(255)")
    private String domain;

    @NotBlank(message = "Роль не может быть пустой")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "Роль должна быть: USER или ADMIN")
    @Column(nullable = false, columnDefinition = "NVARCHAR(20)")
    private String role;

    @NotNull(message = "Дата регистрации не может быть пустой")
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;


    public Person(String login, String password, String domain, String role, LocalDateTime registrationDate) {
        this.login = login;
        this.password = password;
        this.domain = domain;
        this.role = role;
        this.registrationDate = registrationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public PersonDTO getDTO() {
        return new PersonDTO(login, domain, role, registrationDate);
    }
}
