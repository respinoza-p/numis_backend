package com.numismatica.paises.mapper;

import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.model.Pais;
import org.springframework.stereotype.Component;

/**
 * Mapper manual entre entidad Pais y DTOs.
 * Se prefiere mapper manual sobre MapStruct para reducir dependencias en el MVP.
 */
@Component
public class PaisMapper {

    /**
     * Convierte una entidad Pais a PaisDTO.
     */
    public PaisDTO toDto(Pais pais) {
        if (pais == null) return null;

        return PaisDTO.builder()
                .id(pais.getId())
                .codigoIso(pais.getCodigoIso())
                .codigoIso3(pais.getCodigoIso3())
                .nombre(pais.getNombre())
                .nombreOficial(pais.getNombreOficial())
                .capital(pais.getCapital())
                .continente(pais.getContinente())
                .region(pais.getRegion())
                .moneda(pais.getMoneda())
                .codigoTelefono(pais.getCodigoTelefono())
                .banderaEmoji(pais.getBanderaEmoji())
                .idiomas(pais.getIdiomas())
                .fechaCreacion(pais.getFechaCreacion())
                .fechaActualizacion(pais.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte un PaisRequestDTO a entidad Pais (nuevo).
     */
    public Pais toEntity(PaisRequestDTO dto) {
        if (dto == null) return null;

        return Pais.builder()
                .codigoIso(dto.getCodigoIso())
                .codigoIso3(dto.getCodigoIso3())
                .nombre(dto.getNombre())
                .nombreOficial(dto.getNombreOficial())
                .capital(dto.getCapital())
                .continente(dto.getContinente())
                .region(dto.getRegion())
                .moneda(dto.getMoneda())
                .codigoTelefono(dto.getCodigoTelefono())
                .banderaEmoji(dto.getBanderaEmoji())
                .idiomas(dto.getIdiomas())
                .activo(true)
                .build();
    }

    /**
     * Actualiza una entidad Pais existente con datos del DTO.
     */
    public void updateEntity(Pais pais, PaisRequestDTO dto) {
        if (pais == null || dto == null) return;

        pais.setCodigoIso(dto.getCodigoIso());
        pais.setCodigoIso3(dto.getCodigoIso3());
        pais.setNombre(dto.getNombre());
        pais.setNombreOficial(dto.getNombreOficial());
        pais.setCapital(dto.getCapital());
        pais.setContinente(dto.getContinente());
        pais.setRegion(dto.getRegion());
        pais.setMoneda(dto.getMoneda());
        pais.setCodigoTelefono(dto.getCodigoTelefono());
        pais.setBanderaEmoji(dto.getBanderaEmoji());
        pais.setIdiomas(dto.getIdiomas());
    }
}
