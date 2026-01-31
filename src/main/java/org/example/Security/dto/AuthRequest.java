package org.example.Security.dto;

public record AuthRequest(
        String login,
        String password
) {

}
