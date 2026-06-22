package com.numismatica.paises.controller;

import com.numismatica.paises.dto.*;
import com.numismatica.paises.security.RateLimiter;
import com.numismatica.paises.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticación.
 * Endpoints públicos para login y registro de usuarios.
 * Protegido con rate limiting contra ataques de fuerza bruta (EH-04).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de login y registro")
public class AuthController {

    private final AuthService authService;
    private final RateLimiter rateLimiter;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        // EH-04: Rate limiting
        if (!rateLimiter.isAllowed(request)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Demasiados intentos de login. Intente de nuevo en 15 minutos."));
        }

        AuthResponse authResponse = authService.login(loginRequest);
        rateLimiter.resetFor(request); // Reset tras login exitoso
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con rol USER y retorna un token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {

        // EH-04: Rate limiting también en registro
        if (!rateLimiter.isAllowed(request)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Demasiados intentos de registro. Intente de nuevo en 15 minutos."));
        }

        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario registrado exitosamente", authResponse));
    }
}
