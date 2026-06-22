package com.numismatica.paises.exception;

/**
 * Excepción lanzada cuando se intenta crear un país con un código ISO que ya existe.
 */
public class PaisDuplicadoException extends RuntimeException {

    public PaisDuplicadoException(String codigoIso) {
        super("Ya existe un país activo con el código ISO: " + codigoIso);
    }
}
