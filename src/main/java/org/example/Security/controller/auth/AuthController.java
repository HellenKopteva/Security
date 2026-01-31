package org.example.Security.controller.auth;

import org.example.Security.dto.*;
import org.example.Security.security.JwtService;
import org.example.Security.service.user.PersonService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PersonService personService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          PersonService personService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.personService = personService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                )
        );

        String role = auth.getAuthorities().iterator().next().getAuthority();

        return new AuthResponse(
                jwtService.generateAccessToken(auth.getName(), role),
                jwtService.generateRefreshToken(auth.getName())
        );
    }

    @PostMapping("/register")
    public PersonDto register(@RequestBody RegisterRequest request) {
        return personService.createUser(
                request.login(),
                request.password(),
                request.email(),
                false
        );
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {

        var claims = jwtService.parseToken(request.refreshToken());
        String username = claims.getSubject();

        String role = "ROLE_USER";

        return new AuthResponse(
                jwtService.generateAccessToken(username, role),
                jwtService.generateRefreshToken(username)
        );
    }
}
