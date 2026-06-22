package com.numismatica.paises.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de entrada para crear/actualizar un País.
 * Incluye validaciones Jakarta Bean Validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaisRequestDTO {

    @NotBlank(message = "El código ISO es obligatorio")
    @Size(min = 2, max = 2, message = "El código ISO debe tener exactamente 2 caracteres")
    @Pattern(regexp = "^[A-Z]{2}$", message = "El código ISO debe ser 2 letras mayúsculas (ej: CL)")
    private String codigoIso;

    @Size(min = 3, max = 3, message = "El código ISO3 debe tener exactamente 3 caracteres")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código ISO3 debe ser 3 letras mayúsculas (ej: CHL)")
    private String codigoIso3;

    @NotBlank(message = "El nombre del país es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 150, message = "El nombre oficial no puede exceder 150 caracteres")
    private String nombreOficial;

    @Size(max = 100, message = "La capital no puede exceder 100 caracteres")
    private String capital;

    @NotBlank(message = "El continente es obligatorio")
    private String continente;

    private String region;

    private String moneda;

    @Pattern(regexp = "^\\+?\\d{1,4}$", message = "El código telefónico debe ser numérico con prefijo + opcional")
    private String codigoTelefono;

    private String banderaEmoji;

    private List<String> idiomas;
}
