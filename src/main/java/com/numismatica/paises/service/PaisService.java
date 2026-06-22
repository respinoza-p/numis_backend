package com.numismatica.paises.service;

import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de países.
 * Define las operaciones de negocio disponibles.
 */
public interface PaisService {

    Page<PaisDTO> listarTodos(Pageable pageable);

    PaisDTO buscarPorId(String id);

    PaisDTO buscarPorCodigoIso(String codigoIso);

    PaisDTO crear(PaisRequestDTO requestDTO);

    PaisDTO actualizar(String id, PaisRequestDTO requestDTO);

    void eliminar(String id);

    List<PaisDTO> buscarPorNombre(String nombre);

    List<PaisDTO> buscarPorContinente(String continente);
}
