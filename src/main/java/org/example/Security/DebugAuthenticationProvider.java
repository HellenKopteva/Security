package org.example.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DebugAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(DebugAuthenticationProvider.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        log.info("=== ПРОЦЕСС АУТЕНТИФИКАЦИИ ===");
        log.info("Введенный логин: {}", username);
        log.info("Введенный пароль (сырой): {}", rawPassword);

        // Загружаем пользователя
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Получаем хэш из БД
        String storedPassword = userDetails.getPassword();
        log.info("Пароль из БД (хэш): {}", storedPassword);

        // Проверяем пароль
        boolean passwordMatches = passwordEncoder.matches(rawPassword, storedPassword);

        log.info("Совпадение паролей: {}", passwordMatches ? "ДА" : "НЕТ");

        if (!passwordMatches) {
            log.error("Неверный пароль для пользователя: {}", username);
            throw new BadCredentialsException("Неверный пароль");
        }

        log.info("Аутентификация успешна для: {}", username);
        log.info("=== КОНЕЦ ПРОЦЕССА АУТЕНТИФИКАЦИИ ===");

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // credentials очищаются
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}