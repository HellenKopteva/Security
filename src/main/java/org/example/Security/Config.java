package org.example.Security;

import com.github.javafaker.Faker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class Config {
    @Bean
    public Faker faker(){
        return new Faker();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("WhoAreYou");

        return http
                .httpBasic(config -> config
                        .authenticationEntryPoint(basicAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll() // Разрешаем доступ к корню
                        .anyRequest().authenticated() // Все остальные требуют аутентификации
                )
                // Добавляем фильтр для перенаправления с корня на unauthorized
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    FilterChain filterChain)
                            throws ServletException, IOException {

                        // Проверяем, если запрос не содержит стартовую директорию отправляем в заглушку
                        if (!request.getRequestURI().contains("/api/home/")) {

                            // Перенаправляем на страницу unauthorized
                            response.sendRedirect("http://localhost:8080/api/home/loginNew");
                            return;
                        }

                        filterChain.doFilter(request, response);
                    }
                }, BasicAuthenticationFilter.class)
                .build();
    }

}
