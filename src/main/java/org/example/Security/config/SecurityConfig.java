package org.example.Security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/loginNew", "/css/**", "/js/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/home/**", "/api/profile/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/api/loginNew")
                        .loginProcessingUrl("/api/login")
                        .defaultSuccessUrl("/api/home")
                        .failureUrl("/api/loginNew?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl("/api/loginNew?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
