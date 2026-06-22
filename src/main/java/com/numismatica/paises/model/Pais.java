package com.numismatica.paises.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Entidad País — Documento MongoDB.
 * Representa un país con su información geográfica, política y cultural.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "paises")
public class Pais {

    @Id
    private String id;

    @Indexed(unique = true)
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

    @Builder.Default
    private boolean activo = true;

    @CreatedDate
    private Instant fechaCreacion;

    @LastModifiedDate
    private Instant fechaActualizacion;
}
