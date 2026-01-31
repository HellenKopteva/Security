package org.example.Security.dto;

import java.time.LocalDateTime;

public record PersonDto(
        String login,
        String email,
        String role,
        LocalDateTime registrationDate
) {

}
