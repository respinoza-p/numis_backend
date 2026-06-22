package com.numismatica.paises.config;

import com.numismatica.paises.security.JwtAuthEntryPoint;
import com.numismatica.paises.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuración de Spring Security.
 *
 * Política de acceso:
 *   - GET /api/v1/paises/** → Público (lectura abierta)
 *   - POST/PUT/DELETE /api/v1/paises/** → Requiere ROLE_ADMIN
 *   - POST /api/v1/auth/** → Público (login/registro)
 *   - Swagger / OpenAPI → Público
 *   - Actuator health → Público
 *   - Todo lo demás → Autenticado
 *
 * CSRF deshabilitado: esta API es stateless (JWT) y no usa cookies de sesión.
 * La protección CSRF no aplica a APIs REST que usan Bearer tokens.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF deshabilitado — API stateless con JWT (sin cookies de sesión)
            .csrf(csrf -> csrf.disable())

            // EH-06: Headers de seguridad HTTP
            .headers(headers -> headers
                .contentTypeOptions(ct -> {})                    // X-Content-Type-Options: nosniff
                .frameOptions(fo -> fo.deny())                   // X-Frame-Options: DENY
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))                   // HSTS: 1 año
                .referrerPolicy(rp -> rp
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicy(pp -> pp
                    .policy("geolocation=(), microphone=(), camera=()"))
            )

            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints — Públicos
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Swagger / OpenAPI — Públicos
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // Actuator — Solo health público (EH-09)
                .requestMatchers("/actuator/health").permitAll()

                // Países — GET público, escritura requiere ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/paises/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/paises/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/paises/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/paises/**").hasRole("ADMIN")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 para mayor seguridad
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
