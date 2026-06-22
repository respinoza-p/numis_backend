package com.numismatica.paises.mapper;

import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.model.Pais;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para PaisMapper.
 */
class PaisMapperTest {

    private final PaisMapper mapper = new PaisMapper();

    @Test
    @DisplayName("toDto — Debe mapear todos los campos correctamente")
    void debeMappearTodosLosCampos() {
        Pais pais = Pais.builder()
                .id("id-1")
                .codigoIso("CL")
                .codigoIso3("CHL")
                .nombre("Chile")
                .nombreOficial("República de Chile")
                .capital("Santiago")
                .continente("América del Sur")
                .region("Sudamérica")
                .moneda("CLP")
                .codigoTelefono("+56")
                .banderaEmoji("🇨🇱")
                .idiomas(List.of("Español"))
                .fechaCreacion(Instant.now())
                .fechaActualizacion(Instant.now())
                .build();

        PaisDTO dto = mapper.toDto(pais);

        assertThat(dto.getId()).isEqualTo("id-1");
        assertThat(dto.getCodigoIso()).isEqualTo("CL");
        assertThat(dto.getNombre()).isEqualTo("Chile");
        assertThat(dto.getCapital()).isEqualTo("Santiago");
        assertThat(dto.getIdiomas()).containsExactly("Español");
    }

    @Test
    @DisplayName("toDto — Debe retornar null para entrada null")
    void debeRetornarNullParaNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("toEntity — Debe crear entidad desde DTO request")
    void debeCrearEntidadDesdeRequest() {
        PaisRequestDTO request = PaisRequestDTO.builder()
                .codigoIso("AR")
                .nombre("Argentina")
                .continente("América del Sur")
                .build();

        Pais pais = mapper.toEntity(request);

        assertThat(pais.getCodigoIso()).isEqualTo("AR");
        assertThat(pais.getNombre()).isEqualTo("Argentina");
        assertThat(pais.isActivo()).isTrue();
        assertThat(pais.getId()).isNull();
    }

    @Test
    @DisplayName("updateEntity — Debe actualizar campos de la entidad")
    void debeActualizarCampos() {
        Pais pais = Pais.builder()
                .id("id-1")
                .codigoIso("CL")
                .nombre("Chile")
                .capital("Santiago")
                .continente("América del Sur")
                .build();

        PaisRequestDTO update = PaisRequestDTO.builder()
                .codigoIso("CL")
                .nombre("Chile Actualizado")
                .capital("Valparaíso")
                .continente("América del Sur")
                .build();

        mapper.updateEntity(pais, update);

        assertThat(pais.getNombre()).isEqualTo("Chile Actualizado");
        assertThat(pais.getCapital()).isEqualTo("Valparaíso");
        assertThat(pais.getId()).isEqualTo("id-1"); // ID no cambia
    }
}
