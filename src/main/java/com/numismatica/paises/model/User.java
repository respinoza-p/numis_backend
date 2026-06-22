package com.numismatica.paises.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

/**
 * Entidad Usuario — Documento MongoDB.
 * Almacena credenciales y roles para autenticación JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String nombreCompleto;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.ROLE_USER);

    @Builder.Default
    private boolean activo = true;

    @CreatedDate
    private Instant fechaCreacion;

    /**
     * Roles disponibles en el sistema.
     */
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }
}
