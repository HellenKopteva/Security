package org.example.Security.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {

}
