package com.numismatica.paises.service;

import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.exception.PaisDuplicadoException;
import com.numismatica.paises.exception.PaisNotFoundException;
import com.numismatica.paises.mapper.PaisMapper;
import com.numismatica.paises.model.Pais;
import com.numismatica.paises.repository.PaisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PaisServiceImpl.
 * Usa Mockito para simular el repositorio.
 */
@ExtendWith(MockitoExtension.class)
class PaisServiceImplTest {

    @Mock
    private PaisRepository paisRepository;

    @Spy
    private PaisMapper paisMapper = new PaisMapper();

    @InjectMocks
    private PaisServiceImpl paisService;

    private Pais paisChile;
    private Pais paisArgentina;
    private PaisRequestDTO requestChile;

    @BeforeEach
    void setUp() {
        paisChile = Pais.builder()
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
                .activo(true)
                .fechaCreacion(Instant.now())
                .fechaActualizacion(Instant.now())
                .build();

        paisArgentina = Pais.builder()
                .id("id-argentina")
                .codigoIso("AR")
                .codigoIso3("ARG")
                .nombre("Argentina")
                .capital("Buenos Aires")
                .continente("América del Sur")
                .activo(true)
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
    // LISTAR TODOS
    // =========================================================================
    @Nested
    @DisplayName("listarTodos()")
    class ListarTodos {

        @Test
        @DisplayName("Debe retornar página de países activos")
        void debeRetornarPaginaDePaises() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Pais> page = new PageImpl<>(List.of(paisChile, paisArgentina), pageable, 2);

            when(paisRepository.findByActivoTrue(pageable)).thenReturn(page);

            Page<PaisDTO> result = paisService.listarTodos(pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getCodigoIso()).isEqualTo("CL");
            assertThat(result.getContent().get(1).getCodigoIso()).isEqualTo("AR");
            verify(paisRepository).findByActivoTrue(pageable);
        }

        @Test
        @DisplayName("Debe retornar página vacía cuando no hay países")
        void debeRetornarPaginaVacia() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Pais> page = new PageImpl<>(List.of(), pageable, 0);

            when(paisRepository.findByActivoTrue(pageable)).thenReturn(page);

            Page<PaisDTO> result = paisService.listarTodos(pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // =========================================================================
    // BUSCAR POR ID
    // =========================================================================
    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Debe retornar país cuando existe")
        void debeRetornarPaisCuandoExiste() {
            when(paisRepository.findByIdAndActivoTrue("id-chile")).thenReturn(Optional.of(paisChile));

            PaisDTO result = paisService.buscarPorId("id-chile");

            assertThat(result.getCodigoIso()).isEqualTo("CL");
            assertThat(result.getNombre()).isEqualTo("Chile");
            assertThat(result.getCapital()).isEqualTo("Santiago");
        }

        @Test
        @DisplayName("Debe lanzar PaisNotFoundException cuando no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            when(paisRepository.findByIdAndActivoTrue("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paisService.buscarPorId("id-inexistente"))
                    .isInstanceOf(PaisNotFoundException.class)
                    .hasMessageContaining("id-inexistente");
        }
    }

    // =========================================================================
    // BUSCAR POR CÓDIGO ISO
    // =========================================================================
    @Nested
    @DisplayName("buscarPorCodigoIso()")
    class BuscarPorCodigoIso {

        @Test
        @DisplayName("Debe retornar país por código ISO")
        void debeRetornarPaisPorCodigoIso() {
            when(paisRepository.findByCodigoIsoAndActivoTrue("CL")).thenReturn(Optional.of(paisChile));

            PaisDTO result = paisService.buscarPorCodigoIso("CL");

            assertThat(result.getNombre()).isEqualTo("Chile");
        }

        @Test
        @DisplayName("Debe convertir código ISO a mayúsculas")
        void debeConvertirCodigoIsoAMayusculas() {
            when(paisRepository.findByCodigoIsoAndActivoTrue("CL")).thenReturn(Optional.of(paisChile));

            PaisDTO result = paisService.buscarPorCodigoIso("cl");

            assertThat(result.getCodigoIso()).isEqualTo("CL");
            verify(paisRepository).findByCodigoIsoAndActivoTrue("CL");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando código ISO no existe")
        void debeLanzarExcepcionCuandoCodigoIsoNoExiste() {
            when(paisRepository.findByCodigoIsoAndActivoTrue("XX")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paisService.buscarPorCodigoIso("XX"))
                    .isInstanceOf(PaisNotFoundException.class)
                    .hasMessageContaining("XX");
        }
    }

    // =========================================================================
    // CREAR
    // =========================================================================
    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear país exitosamente")
        void debeCrearPaisExitosamente() {
            when(paisRepository.existsByCodigoIsoAndActivoTrue("CL")).thenReturn(false);
            when(paisRepository.save(any(Pais.class))).thenReturn(paisChile);

            PaisDTO result = paisService.crear(requestChile);

            assertThat(result.getCodigoIso()).isEqualTo("CL");
            assertThat(result.getNombre()).isEqualTo("Chile");
            verify(paisRepository).save(any(Pais.class));
        }

        @Test
        @DisplayName("Debe lanzar PaisDuplicadoException si código ISO ya existe")
        void debeLanzarExcepcionSiCodigoIsoDuplicado() {
            when(paisRepository.existsByCodigoIsoAndActivoTrue("CL")).thenReturn(true);

            assertThatThrownBy(() -> paisService.crear(requestChile))
                    .isInstanceOf(PaisDuplicadoException.class)
                    .hasMessageContaining("CL");

            verify(paisRepository, never()).save(any());
        }
    }

    // =========================================================================
    // ACTUALIZAR
    // =========================================================================
    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar país existente")
        void debeActualizarPaisExistente() {
            when(paisRepository.findByIdAndActivoTrue("id-chile")).thenReturn(Optional.of(paisChile));
            when(paisRepository.save(any(Pais.class))).thenReturn(paisChile);

            PaisRequestDTO updateRequest = PaisRequestDTO.builder()
                    .codigoIso("CL")
                    .nombre("Chile Actualizado")
                    .continente("América del Sur")
                    .build();

            PaisDTO result = paisService.actualizar("id-chile", updateRequest);

            assertThat(result).isNotNull();
            verify(paisRepository).save(any(Pais.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al cambiar ISO a uno duplicado")
        void debeLanzarExcepcionAlCambiarIsoADuplicado() {
            when(paisRepository.findByIdAndActivoTrue("id-chile")).thenReturn(Optional.of(paisChile));
            when(paisRepository.existsByCodigoIsoAndActivoTrue("AR")).thenReturn(true);

            PaisRequestDTO updateRequest = PaisRequestDTO.builder()
                    .codigoIso("AR")
                    .nombre("Chile")
                    .continente("América del Sur")
                    .build();

            assertThatThrownBy(() -> paisService.actualizar("id-chile", updateRequest))
                    .isInstanceOf(PaisDuplicadoException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción si país a actualizar no existe")
        void debeLanzarExcepcionSiPaisNoExiste() {
            when(paisRepository.findByIdAndActivoTrue("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paisService.actualizar("id-inexistente", requestChile))
                    .isInstanceOf(PaisNotFoundException.class);
        }
    }

    // =========================================================================
    // ELIMINAR (SOFT DELETE)
    // =========================================================================
    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe marcar país como inactivo (soft delete)")
        void debeMarcarPaisComoInactivo() {
            when(paisRepository.findByIdAndActivoTrue("id-chile")).thenReturn(Optional.of(paisChile));
            when(paisRepository.save(any(Pais.class))).thenReturn(paisChile);

            paisService.eliminar("id-chile");

            assertThat(paisChile.isActivo()).isFalse();
            verify(paisRepository).save(paisChile);
        }

        @Test
        @DisplayName("Debe lanzar excepción si país a eliminar no existe")
        void debeLanzarExcepcionSiPaisNoExiste() {
            when(paisRepository.findByIdAndActivoTrue("id-inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paisService.eliminar("id-inexistente"))
                    .isInstanceOf(PaisNotFoundException.class);
        }
    }

    // =========================================================================
    // BÚSQUEDAS
    // =========================================================================
    @Nested
    @DisplayName("Búsquedas")
    class Busquedas {

        @Test
        @DisplayName("Debe buscar países por nombre parcial")
        void debeBuscarPorNombre() {
            when(paisRepository.findByNombreContainingIgnoreCaseAndActivoTrue("chi"))
                    .thenReturn(List.of(paisChile));

            List<PaisDTO> result = paisService.buscarPorNombre("chi");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Chile");
        }

        @Test
        @DisplayName("Debe buscar países por continente")
        void debeBuscarPorContinente() {
            when(paisRepository.findByContinenteIgnoreCaseAndActivoTrue("América del Sur"))
                    .thenReturn(List.of(paisChile, paisArgentina));

            List<PaisDTO> result = paisService.buscarPorContinente("América del Sur");

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay resultados")
        void debeRetornarListaVacia() {
            when(paisRepository.findByNombreContainingIgnoreCaseAndActivoTrue("xyz"))
                    .thenReturn(List.of());

            List<PaisDTO> result = paisService.buscarPorNombre("xyz");

            assertThat(result).isEmpty();
        }
    }
}
