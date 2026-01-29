package org.example.Security;


import java.time.LocalDateTime;

public class PersonDTO { //Сделал проверки данных уже внутри персона
    private String login;
    private String domain;
    private String role;
    private LocalDateTime registrationDate;

    public PersonDTO(String login, String domain, String role, LocalDateTime registrationDate) {
        this.login = login;
        this.domain = domain;
        this.role = role;
        this.registrationDate = registrationDate;
    }

    public String getLogin() {
        return login;
    }

    public String getDomain() {
        return domain;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

}
