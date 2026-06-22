package com.numismatica.paises;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test básico de la clase principal.
 * Los tests de integración completa del contexto requieren una instancia de MongoDB.
 * Esto se valida en el pipeline CI/CD con la BD real o con Testcontainers.
 */
class PaisesApiApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(PaisesApiApplication.class).isNotNull();
    }
}
