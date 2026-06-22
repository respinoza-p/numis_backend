package com.numismatica.paises.repository;

import com.numismatica.paises.model.Pais;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad País.
 */
@Repository
public interface PaisRepository extends MongoRepository<Pais, String> {

    Optional<Pais> findByCodigoIsoAndActivoTrue(String codigoIso);

    Page<Pais> findByActivoTrue(Pageable pageable);

    List<Pais> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Pais> findByContinenteIgnoreCaseAndActivoTrue(String continente);

    boolean existsByCodigoIsoAndActivoTrue(String codigoIso);

    Optional<Pais> findByIdAndActivoTrue(String id);
}
