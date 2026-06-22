package com.numismatica.paises.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuración de MongoDB.
 * Habilita auditoría automática (@CreatedDate, @LastModifiedDate).
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
