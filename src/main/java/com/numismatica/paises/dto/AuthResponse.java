package com.numismatica.paises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO de respuesta de autenticación.
 * Contiene el token JWT y la información básica del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private String username;
    private String email;
    private Set<String> roles;

    /**
     * Crea un AuthResponse con Bearer token.
     */
    public static AuthResponse of(String token, String username, String email, Set<String> roles) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(username)
                .email(email)
                .roles(roles)
                .build();
    }
}
