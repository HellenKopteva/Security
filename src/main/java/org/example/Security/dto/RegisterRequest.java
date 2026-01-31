package org.example.Security.dto;

public record RegisterRequest(
        String login,
        String password,
        String email
) {

}
