package com.numismatica.paises.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO de respuesta para País.
 * Se utiliza para exponer datos al cliente sin revelar campos internos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaisDTO {

    private String id;
    private String codigoIso;
    private String codigoIso3;
    private String nombre;
    private String nombreOficial;
    private String capital;
    private String continente;
    private String region;
    private String moneda;
    private String codigoTelefono;
    private String banderaEmoji;
    private List<String> idiomas;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;
}
