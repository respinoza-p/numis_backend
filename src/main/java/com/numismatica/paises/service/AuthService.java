package com.numismatica.paises.service;

import com.numismatica.paises.dto.AuthResponse;
import com.numismatica.paises.dto.LoginRequest;
import com.numismatica.paises.dto.RegisterRequest;
import com.numismatica.paises.model.User;
import com.numismatica.paises.repository.UserRepository;
import com.numismatica.paises.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación.
 * Maneja registro de usuarios y generación de tokens JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Autentica un usuario y retorna un token JWT.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow();

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        log.info("Login exitoso para usuario: {}", loginRequest.getUsername());
        return AuthResponse.of(token, user.getUsername(), user.getEmail(), roles);
    }

    /**
     * Registra un nuevo usuario.
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registro de nuevo usuario: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso: " + registerRequest.getUsername());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso: " + registerRequest.getEmail());
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nombreCompleto(registerRequest.getNombreCompleto())
                .roles(Set.of(User.Role.ROLE_USER))
                .activo(true)
                .build();

        userRepository.save(user);

        // Autenticar automáticamente tras registro
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        log.info("Usuario registrado exitosamente: {}", registerRequest.getUsername());
        return AuthResponse.of(token, user.getUsername(), user.getEmail(), roles);
    }
}
