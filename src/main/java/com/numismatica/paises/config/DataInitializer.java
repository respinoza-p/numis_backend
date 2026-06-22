package com.numismatica.paises.config;

import com.numismatica.paises.model.Pais;
import com.numismatica.paises.model.User;
import com.numismatica.paises.repository.PaisRepository;
import com.numismatica.paises.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Inicializador de datos de prueba para la base de datos MongoDB.
 * Carga 50 países (incluyendo Chile) y usuarios de prueba si las colecciones están vacías.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PaisRepository paisRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando verificación de datos de prueba en MongoDB...");

        // 1. Inicializar Usuarios
        if (userRepository.count() == 0) {
            log.info("La colección de usuarios está vacía. Creando usuarios por defecto...");

            User admin = User.builder()
                    .username("admin")
                    .email("admin@numismatica.com")
                    .password(passwordEncoder.encode("Admin123*"))
                    .nombreCompleto("Administrador del Sistema")
                    .roles(Set.of(User.Role.ROLE_ADMIN, User.Role.ROLE_USER))
                    .activo(true)
                    .build();

            User user = User.builder()
                    .username("user")
                    .email("user@numismatica.com")
                    .password(passwordEncoder.encode("User1234*"))
                    .nombreCompleto("Usuario Regular")
                    .roles(Set.of(User.Role.ROLE_USER))
                    .activo(true)
                    .build();

            userRepository.saveAll(List.of(admin, user));
            log.info("Usuarios creados exitosamente: 'admin' (clave: Admin123*) y 'user' (clave: User1234*)");
        } else {
            log.info("La colección de usuarios ya contiene registros ({}). Se omite el sembrado.", userRepository.count());
        }

        // 2. Inicializar Países (50 países)
        if (paisRepository.count() == 0) {
            log.info("La colección de países está vacía. Sembrando masa de datos de 50 países...");

            List<Pais> paises = new ArrayList<>();

            // América del Sur
            paises.add(createPais("CL", "CHL", "Chile", "República de Chile", "Santiago", "América", "Sudamérica", "CLP", "+56", "🇨🇱", List.of("Español")));
            paises.add(createPais("AR", "ARG", "Argentina", "República Argentina", "Buenos Aires", "América", "Sudamérica", "ARS", "+54", "🇦🇷", List.of("Español")));
            paises.add(createPais("PE", "PER", "Perú", "República del Perú", "Lima", "América", "Sudamérica", "PEN", "+51", "🇵🇪", List.of("Español", "Quechua")));
            paises.add(createPais("BR", "BRA", "Brasil", "República Federativa del Brasil", "Brasilia", "América", "Sudamérica", "BRL", "+55", "🇧🇷", List.of("Portugués")));
            paises.add(createPais("CO", "COL", "Colombia", "República de Colombia", "Bogotá", "América", "Sudamérica", "COP", "+57", "🇨🇴", List.of("Español")));
            paises.add(createPais("EC", "ECU", "Ecuador", "República del Ecuador", "Quito", "América", "Sudamérica", "USD", "+593", "🇪🇨", List.of("Español")));
            paises.add(createPais("BO", "BOL", "Bolivia", "Estado Plurinacional de Bolivia", "Sucre", "América", "Sudamérica", "BOB", "+591", "🇧🇴", List.of("Español", "Quechua", "Aymara")));
            paises.add(createPais("UY", "URY", "Uruguay", "República Oriental del Uruguay", "Montevideo", "América", "Sudamérica", "UYU", "+598", "🇺🇾", List.of("Español")));
            paises.add(createPais("PY", "PRY", "Paraguay", "República del Paraguay", "Asunción", "América", "Sudamérica", "PYG", "+595", "🇵🇾", List.of("Español", "Guaraní")));
            paises.add(createPais("VE", "VEN", "Venezuela", "República Bolivariana de Venezuela", "Caracas", "América", "Sudamérica", "VES", "+58", "🇻🇪", List.of("Español")));

            // América del Norte y Central
            paises.add(createPais("MX", "MEX", "México", "Estados Unidos Mexicanos", "Ciudad de México", "América", "Norteamérica", "MXN", "+52", "🇲🇽", List.of("Español")));
            paises.add(createPais("CA", "CAN", "Canadá", "Canadá", "Ottawa", "América", "Norteamérica", "CAD", "+1", "🇨🇦", List.of("Inglés", "Francés")));
            paises.add(createPais("US", "USA", "Estados Unidos", "Estados Unidos de América", "Washington D.C.", "América", "Norteamérica", "USD", "+1", "🇺🇸", List.of("Inglés")));
            paises.add(createPais("CR", "CRI", "Costa Rica", "República de Costa Rica", "San José", "América", "Centroamérica", "CRC", "+506", "🇨🇷", List.of("Español")));
            paises.add(createPais("PA", "PAN", "Panamá", "República de Panamá", "Ciudad de Panamá", "América", "Centroamérica", "PAB", "+507", "🇵🇦", List.of("Español")));
            paises.add(createPais("GT", "GTM", "Guatemala", "República de Guatemala", "Ciudad de Guatemala", "América", "Centroamérica", "GTQ", "+502", "🇬🇹", List.of("Español")));
            paises.add(createPais("HN", "HND", "Honduras", "República de Honduras", "Tegucigalpa", "América", "Centroamérica", "HNL", "+504", "🇭🇳", List.of("Español")));
            paises.add(createPais("SV", "SLV", "El Salvador", "República de El Salvador", "San Salvador", "América", "Centroamérica", "USD", "+503", "🇸🇻", List.of("Español")));
            paises.add(createPais("CU", "CUB", "Cuba", "República de Cuba", "La Habana", "América", "Caribe", "CUP", "+53", "🇨🇺", List.of("Español")));
            paises.add(createPais("DO", "DOM", "República Dominicana", "República Dominicana", "Santo Domingo", "América", "Caribe", "DOP", "+1", "🇩🇴", List.of("Español")));

            // Europa
            paises.add(createPais("ES", "ESP", "España", "Reino de España", "Madrid", "Europa", "Europa del Sur", "EUR", "+34", "🇪🇸", List.of("Español")));
            paises.add(createPais("FR", "FRA", "Francia", "República Francesa", "París", "Europa", "Europa Occidental", "EUR", "+33", "🇫🇷", List.of("Francés")));
            paises.add(createPais("DE", "DEU", "Alemania", "República Federal de Alemania", "Berlín", "Europa", "Europa Central", "EUR", "+49", "🇩🇪", List.of("Alemán")));
            paises.add(createPais("IT", "ITA", "Italia", "República Italiana", "Roma", "Europa", "Europa del Sur", "EUR", "+39", "🇮🇹", List.of("Italiano")));
            paises.add(createPais("GB", "GBR", "Reino Unido", "Reino Unido de Gran Bretaña e Irlanda del Norte", "Londres", "Europa", "Europa Occidental", "GBP", "+44", "🇬🇧", List.of("Inglés")));
            paises.add(createPais("PT", "PRT", "Portugal", "República Portuguesa", "Lisboa", "Europa", "Europa del Sur", "EUR", "+351", "🇵🇹", List.of("Portugués")));
            paises.add(createPais("NL", "NLD", "Países Bajos", "Reino de los Países Bajos", "Ámsterdam", "Europa", "Europa Occidental", "EUR", "+31", "🇳🇱", List.of("Neerlandés")));
            paises.add(createPais("BE", "BEL", "Bélgica", "Reino de Bélgica", "Bruselas", "Europa", "Europa Occidental", "EUR", "+32", "🇧🇪", List.of("Neerlandés", "Francés", "Alemán")));
            paises.add(createPais("CH", "CHE", "Suiza", "Confederación Suiza", "Berna", "Europa", "Europa Occidental", "CHF", "+41", "🇨🇭", List.of("Alemán", "Francés", "Italiano", "Romanche")));
            paises.add(createPais("AT", "AUT", "Austria", "República de Austria", "Viena", "Europa", "Europa Central", "EUR", "+43", "🇦🇹", List.of("Alemán")));

            // Asia
            paises.add(createPais("JP", "JPN", "Japón", "Estado de Japón", "Tokio", "Asia", "Asia Oriental", "JPY", "+81", "🇯🇵", List.of("Japonés")));
            paises.add(createPais("CN", "CHN", "China", "República Popular China", "Pekín", "Asia", "Asia Oriental", "CNY", "+86", "🇨🇳", List.of("Chino mandarín")));
            paises.add(createPais("KR", "KOR", "Corea del Sur", "República de Corea", "Seúl", "Asia", "Asia Oriental", "KRW", "+82", "🇰🇷", List.of("Coreano")));
            paises.add(createPais("IN", "IND", "India", "República de la India", "Nueva Delhi", "Asia", "Asia del Sur", "INR", "+91", "🇮🇳", List.of("Hindi", "Inglés")));
            paises.add(createPais("TH", "THA", "Tailandia", "Reino de Tailandia", "Bangkok", "Asia", "Sudeste Asiático", "THB", "+66", "🇹🇭", List.of("Tailandés")));
            paises.add(createPais("VN", "VNM", "Vietnam", "República Socialista de Vietnam", "Hanói", "Asia", "Sudeste Asiático", "VND", "+84", "🇻🇳", List.of("Vietnamita")));
            paises.add(createPais("ID", "IDN", "Indonesia", "República de Indonesia", "Yakarta", "Asia", "Sudeste Asiático", "IDR", "+62", "🇮🇩", List.of("Indonesio")));
            paises.add(createPais("PH", "PHL", "Filipinas", "República de Filipinas", "Manila", "Asia", "Sudeste Asiático", "PHP", "+63", "🇵🇭", List.of("Filipino", "Inglés")));
            paises.add(createPais("SG", "SGP", "Singapur", "República de Singapur", "Singapur", "Asia", "Sudeste Asiático", "SGD", "+65", "🇸🇬", List.of("Inglés", "Malayo", "Chino", "Tamil")));
            paises.add(createPais("MY", "MYS", "Malasia", "Malasia", "Kuala Lumpur", "Asia", "Sudeste Asiático", "MYR", "+60", "🇲🇾", List.of("Malayo")));

            // África
            paises.add(createPais("EG", "EGY", "Egipto", "República Árabe de Egipto", "El Cairo", "África", "Norte de África", "EGP", "+20", "🇪🇬", List.of("Árabe")));
            paises.add(createPais("ZA", "ZAF", "Sudáfrica", "República de Sudáfrica", "Pretoria", "África", "África del Sur", "ZAR", "+27", "🇿🇦", List.of("Zulú", "Xhosa", "Afrikáans", "Inglés")));
            paises.add(createPais("NG", "NGA", "Nigeria", "República Federal de Nigeria", "Abuya", "África", "África Occidental", "NGN", "+234", "🇳🇬", List.of("Inglés")));
            paises.add(createPais("KE", "KEN", "Kenia", "República de Kenia", "Nairobi", "África", "África Oriental", "KES", "+254", "🇰🇪", List.of("Swahili", "Inglés")));
            paises.add(createPais("MA", "MAR", "Marruecos", "Reino de Marruecos", "Rabat", "África", "Norte de África", "MAD", "+212", "🇲🇦", List.of("Árabe", "Bereber")));

            // Oceanía y Transcontinentales
            paises.add(createPais("AU", "AUS", "Australia", "Mancomunidad de Australia", "Canberra", "Oceanía", "Australia y Nueva Zelanda", "AUD", "+61", "🇦🇺", List.of("Inglés")));
            paises.add(createPais("NZ", "NZL", "Nueva Zelanda", "Nueva Zelanda", "Wellington", "Oceanía", "Australia y Nueva Zelanda", "NZD", "+64", "🇳🇿", List.of("Inglés", "Maorí")));
            paises.add(createPais("RU", "RUS", "Rusia", "Federación de Rusia", "Moscú", "Europa", "Europa del Este", "RUB", "+7", "🇷🇺", List.of("Ruso")));
            paises.add(createPais("TR", "TUR", "Turquía", "República de Turquía", "Ankara", "Asia", "Asia Occidental", "TRY", "+90", "🇹🇷", List.of("Turco")));
            paises.add(createPais("SA", "SAU", "Arabia Saudita", "Reino de Arabia Saudita", "Riad", "Asia", "Asia Occidental", "SAR", "+966", "🇸🇦", List.of("Árabe")));

            paisRepository.saveAll(paises);
            log.info("Sembrado de {} países completado exitosamente en la base de datos.", paises.size());
        } else {
            log.info("La colección de países ya contiene registros ({}). Se omite el sembrado.", paisRepository.count());
        }

        log.info("Verificación de datos de prueba completada.");
    }

    private Pais createPais(String codigoIso, String codigoIso3, String nombre, String nombreOficial,
                            String capital, String continente, String region, String moneda,
                            String codigoTelefono, String banderaEmoji, List<String> idiomas) {
        return Pais.builder()
                .codigoIso(codigoIso)
                .codigoIso3(codigoIso3)
                .nombre(nombre)
                .nombreOficial(nombreOficial)
                .capital(capital)
                .continente(continente)
                .region(region)
                .moneda(moneda)
                .codigoTelefono(codigoTelefono)
                .banderaEmoji(banderaEmoji)
                .idiomas(idiomas)
                .activo(true)
                .build();
    }
}
