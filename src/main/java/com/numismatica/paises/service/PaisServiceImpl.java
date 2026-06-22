package com.numismatica.paises.service;

import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.exception.PaisDuplicadoException;
import com.numismatica.paises.exception.PaisNotFoundException;
import com.numismatica.paises.mapper.PaisMapper;
import com.numismatica.paises.model.Pais;
import com.numismatica.paises.repository.PaisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de países.
 * Contiene la lógica de negocio para operaciones CRUD sobre países.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Override
    public Page<PaisDTO> listarTodos(Pageable pageable) {
        log.debug("Listando países activos, página: {}", pageable.getPageNumber());
        return paisRepository.findByActivoTrue(pageable)
                .map(paisMapper::toDto);
    }

    @Override
    public PaisDTO buscarPorId(String id) {
        log.debug("Buscando país por ID: {}", id);
        Pais pais = paisRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> PaisNotFoundException.byId(id));
        return paisMapper.toDto(pais);
    }

    @Override
    public PaisDTO buscarPorCodigoIso(String codigoIso) {
        log.debug("Buscando país por código ISO: {}", codigoIso);
        String isoUpper = codigoIso.toUpperCase();
        Pais pais = paisRepository.findByCodigoIsoAndActivoTrue(isoUpper)
                .orElseThrow(() -> PaisNotFoundException.byCodigoIso(isoUpper));
        return paisMapper.toDto(pais);
    }

    @Override
    public PaisDTO crear(PaisRequestDTO requestDTO) {
        String codigoIso = requestDTO.getCodigoIso().toUpperCase();
        log.info("Creando país con código ISO: {}", codigoIso);

        if (paisRepository.existsByCodigoIsoAndActivoTrue(codigoIso)) {
            throw new PaisDuplicadoException(codigoIso);
        }

        Pais pais = paisMapper.toEntity(requestDTO);
        pais.setCodigoIso(codigoIso);
        if (requestDTO.getCodigoIso3() != null) {
            pais.setCodigoIso3(requestDTO.getCodigoIso3().toUpperCase());
        }

        Pais saved = paisRepository.save(pais);
        log.info("País creado exitosamente: {} ({})", saved.getNombre(), saved.getCodigoIso());
        return paisMapper.toDto(saved);
    }

    @Override
    public PaisDTO actualizar(String id, PaisRequestDTO requestDTO) {
        log.info("Actualizando país con ID: {}", id);

        Pais paisExistente = paisRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> PaisNotFoundException.byId(id));

        // Verificar si el nuevo código ISO ya existe en otro país
        String nuevoCodigoIso = requestDTO.getCodigoIso().toUpperCase();
        if (!paisExistente.getCodigoIso().equals(nuevoCodigoIso)
                && paisRepository.existsByCodigoIsoAndActivoTrue(nuevoCodigoIso)) {
            throw new PaisDuplicadoException(nuevoCodigoIso);
        }

        paisMapper.updateEntity(paisExistente, requestDTO);
        paisExistente.setCodigoIso(nuevoCodigoIso);
        if (requestDTO.getCodigoIso3() != null) {
            paisExistente.setCodigoIso3(requestDTO.getCodigoIso3().toUpperCase());
        }

        Pais updated = paisRepository.save(paisExistente);
        log.info("País actualizado exitosamente: {} ({})", updated.getNombre(), updated.getCodigoIso());
        return paisMapper.toDto(updated);
    }

    @Override
    public void eliminar(String id) {
        log.info("Eliminando país (soft delete) con ID: {}", id);

        Pais pais = paisRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> PaisNotFoundException.byId(id));

        pais.setActivo(false);
        paisRepository.save(pais);
        log.info("País eliminado exitosamente: {} ({})", pais.getNombre(), pais.getCodigoIso());
    }

    @Override
    public List<PaisDTO> buscarPorNombre(String nombre) {
        log.debug("Buscando países por nombre: {}", nombre);
        return paisRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(paisMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaisDTO> buscarPorContinente(String continente) {
        log.debug("Buscando países por continente: {}", continente);
        return paisRepository.findByContinenteIgnoreCaseAndActivoTrue(continente)
                .stream()
                .map(paisMapper::toDto)
                .collect(Collectors.toList());
    }
}
