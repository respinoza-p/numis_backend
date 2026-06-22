package com.numismatica.paises.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numismatica.paises.config.CorsConfig;
import com.numismatica.paises.config.SecurityConfig;
import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.exception.GlobalExceptionHandler;
import com.numismatica.paises.exception.PaisDuplicadoException;
import com.numismatica.paises.exception.PaisNotFoundException;
import com.numismatica.paises.security.CustomUserDetailsService;
import com.numismatica.paises.security.JwtAuthEntryPoint;
import com.numismatica.paises.security.JwtTokenProvider;
import com.numismatica.paises.service.PaisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para PaisController.
 * Usa MockMvc con la cadena de seguridad real para verificar autorización.
 */
@WebMvcTest(PaisController.class)
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, JwtAuthEntryPoint.class})
class PaisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaisService paisService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private PaisDTO paisChileDTO;
    private PaisRequestDTO requestChile;

    @BeforeEach
    void setUp() {
        paisChileDTO = PaisDTO.builder()
                .id("id-chile")
                .codigoIso("CL")
                .codigoIso3("CHL")
                .nombre("Chile")
                .nombreOficial("República de Chile")
                .capital("Santiago")
                .continente("América del Sur")
                .region("Sudamérica")
                .moneda("Peso chileno (CLP)")
                .codigoTelefono("+56")
                .banderaEmoji("🇨🇱")
                .idiomas(List.of("Español"))
                .fechaCreacion(Instant.now())
                .fechaActualizacion(Instant.now())
                .build();

        requestChile = PaisRequestDTO.builder()
                .codigoIso("CL")
                .codigoIso3("CHL")
                .nombre("Chile")
                .nombreOficial("República de Chile")
                .capital("Santiago")
                .continente("América del Sur")
                .region("Sudamérica")
                .moneda("Peso chileno (CLP)")
                .codigoTelefono("+56")
                .banderaEmoji("🇨🇱")
                .idiomas(List.of("Español"))
                .build();
    }

    // =========================================================================
    // GET — ENDPOINTS PÚBLICOS
    // =========================================================================
    @Nested
    @DisplayName("GET /api/v1/paises — Endpoints públicos")
    class EndpointsPublicos {

        @Test
        @DisplayName("GET /api/v1/paises — Debe listar países sin autenticación")
        void debeListarPaisesSinAuth() throws Exception {
            Page<PaisDTO> page = new PageImpl<>(List.of(paisChileDTO));
            when(paisService.listarTodos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/v1/paises"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].codigoIso").value("CL"));
        }

        @Test
        @DisplayName("GET /api/v1/paises/{id} — Debe obtener país por ID sin auth")
        void debeObtenerPaisPorIdSinAuth() throws Exception {
            when(paisService.buscarPorId("id-chile")).thenReturn(paisChileDTO);

            mockMvc.perform(get("/api/v1/paises/id-chile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.nombre").value("Chile"));
        }

        @Test
        @DisplayName("GET /api/v1/paises/iso/{codigoIso} — Debe obtener por ISO sin auth")
        void debeObtenerPorIsoSinAuth() throws Exception {
            when(paisService.buscarPorCodigoIso("CL")).thenReturn(paisChileDTO);

            mockMvc.perform(get("/api/v1/paises/iso/CL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.capital").value("Santiago"));
        }

        @Test
        @DisplayName("GET /api/v1/paises/{id} — Debe retornar 404 si no existe")
        void debeRetornar404SiNoExiste() throws Exception {
            when(paisService.buscarPorId("id-inexistente"))
                    .thenThrow(PaisNotFoundException.byId("id-inexistente"));

            mockMvc.perform(get("/api/v1/paises/id-inexistente"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("GET /api/v1/paises/buscar — Debe buscar por nombre sin auth")
        void debeBuscarPorNombreSinAuth() throws Exception {
            when(paisService.buscarPorNombre("chi")).thenReturn(List.of(paisChileDTO));

            mockMvc.perform(get("/api/v1/paises/buscar").param("nombre", "chi"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }

        @Test
        @DisplayName("GET /api/v1/paises/continente/{continente} — Debe filtrar por continente")
        void debeFiltrarPorContinenteSinAuth() throws Exception {
            when(paisService.buscarPorContinente("América del Sur")).thenReturn(List.of(paisChileDTO));

            mockMvc.perform(get("/api/v1/paises/continente/América del Sur"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)));
        }
    }

    // =========================================================================
    // POST — REQUIERE ADMIN
    // =========================================================================
    @Nested
    @DisplayName("POST /api/v1/paises — Requiere ADMIN")
    class CrearPais {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe crear país con rol ADMIN")
        void debeCrearConRolAdmin() throws Exception {
            when(paisService.crear(any(PaisRequestDTO.class))).thenReturn(paisChileDTO);

            mockMvc.perform(post("/api/v1/paises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.codigoIso").value("CL"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe denegar con rol USER")
        void debeDenegarConRolUser() throws Exception {
            mockMvc.perform(post("/api/v1/paises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Debe denegar sin autenticación")
        void debeDenegarSinAuth() throws Exception {
            mockMvc.perform(post("/api/v1/paises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 400 con datos inválidos")
        void debeRetornar400ConDatosInvalidos() throws Exception {
            PaisRequestDTO invalid = PaisRequestDTO.builder()
                    .codigoIso("")
                    .nombre("")
                    .continente("")
                    .build();

            mockMvc.perform(post("/api/v1/paises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe retornar 409 con código ISO duplicado")
        void debeRetornar409ConIsoDuplicado() throws Exception {
            when(paisService.crear(any())).thenThrow(new PaisDuplicadoException("CL"));

            mockMvc.perform(post("/api/v1/paises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    // =========================================================================
    // PUT — REQUIERE ADMIN
    // =========================================================================
    @Nested
    @DisplayName("PUT /api/v1/paises/{id} — Requiere ADMIN")
    class ActualizarPais {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe actualizar país con rol ADMIN")
        void debeActualizarConRolAdmin() throws Exception {
            when(paisService.actualizar(eq("id-chile"), any())).thenReturn(paisChileDTO);

            mockMvc.perform(put("/api/v1/paises/id-chile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe denegar actualización con rol USER")
        void debeDenegarActualizacionConRolUser() throws Exception {
            mockMvc.perform(put("/api/v1/paises/id-chile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestChile)))
                    .andExpect(status().isForbidden());
        }
    }

    // =========================================================================
    // DELETE — REQUIERE ADMIN
    // =========================================================================
    @Nested
    @DisplayName("DELETE /api/v1/paises/{id} — Requiere ADMIN")
    class EliminarPais {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Debe eliminar país con rol ADMIN")
        void debeEliminarConRolAdmin() throws Exception {
            doNothing().when(paisService).eliminar("id-chile");

            mockMvc.perform(delete("/api/v1/paises/id-chile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Debe denegar eliminación con rol USER")
        void debeDenegarEliminacionConRolUser() throws Exception {
            mockMvc.perform(delete("/api/v1/paises/id-chile"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Debe denegar eliminación sin autenticación")
        void debeDenegarEliminacionSinAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/paises/id-chile"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
