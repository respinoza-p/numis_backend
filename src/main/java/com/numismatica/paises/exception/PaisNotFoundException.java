package com.numismatica.paises.exception;

/**
 * Excepción lanzada cuando un país no es encontrado.
 */
public class PaisNotFoundException extends RuntimeException {

    public PaisNotFoundException(String message) {
        super(message);
    }

    public static PaisNotFoundException byId(String id) {
        return new PaisNotFoundException("País no encontrado con ID: " + id);
    }

    public static PaisNotFoundException byCodigoIso(String codigoIso) {
        return new PaisNotFoundException("País no encontrado con código ISO: " + codigoIso);
    }
}
