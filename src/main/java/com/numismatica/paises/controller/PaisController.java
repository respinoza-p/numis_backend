package com.numismatica.paises.controller;

import com.numismatica.paises.dto.ApiResponse;
import com.numismatica.paises.dto.PaisDTO;
import com.numismatica.paises.dto.PaisRequestDTO;
import com.numismatica.paises.service.PaisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de países.
 *
 * Endpoints GET son públicos (lectura abierta).
 * Endpoints POST/PUT/DELETE requieren autenticación con rol ADMIN.
 */
@RestController
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
@Tag(name = "Países", description = "Operaciones CRUD para la gestión de países")
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    @Operation(summary = "Listar todos los países", description = "Retorna una lista paginada de países activos")
    public ResponseEntity<ApiResponse<Page<PaisDTO>>> listarTodos(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        Page<PaisDTO> paises = paisService.listarTodos(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Países obtenidos exitosamente", paises));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener país por ID")
    public ResponseEntity<ApiResponse<PaisDTO>> buscarPorId(
            @Parameter(description = "ID del país") @PathVariable String id) {
        PaisDTO pais = paisService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("País encontrado", pais));
    }

    @GetMapping("/iso/{codigoIso}")
    @Operation(summary = "Obtener país por código ISO", description = "Busca un país por su código ISO 3166-1 alpha-2")
    public ResponseEntity<ApiResponse<PaisDTO>> buscarPorCodigoIso(
            @Parameter(description = "Código ISO alpha-2 (ej: CL, AR, MX)") @PathVariable String codigoIso) {
        PaisDTO pais = paisService.buscarPorCodigoIso(codigoIso);
        return ResponseEntity.ok(ApiResponse.ok("País encontrado", pais));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar países por nombre", description = "Búsqueda parcial case-insensitive")
    public ResponseEntity<ApiResponse<List<PaisDTO>>> buscarPorNombre(
            @Parameter(description = "Término de búsqueda") @RequestParam String nombre) {
        List<PaisDTO> paises = paisService.buscarPorNombre(nombre);
        return ResponseEntity.ok(ApiResponse.ok("Resultados de búsqueda", paises));
    }

    @GetMapping("/continente/{continente}")
    @Operation(summary = "Filtrar países por continente")
    public ResponseEntity<ApiResponse<List<PaisDTO>>> buscarPorContinente(
            @Parameter(description = "Nombre del continente") @PathVariable String continente) {
        List<PaisDTO> paises = paisService.buscarPorContinente(continente);
        return ResponseEntity.ok(ApiResponse.ok("Países del continente: " + continente, paises));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo país", description = "Requiere rol ADMIN")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "País creado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Código ISO duplicado")
    })
    public ResponseEntity<ApiResponse<PaisDTO>> crear(@Valid @RequestBody PaisRequestDTO requestDTO) {
        PaisDTO pais = paisService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("País creado exitosamente", pais));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un país existente", description = "Requiere rol ADMIN")
    public ResponseEntity<ApiResponse<PaisDTO>> actualizar(
            @Parameter(description = "ID del país") @PathVariable String id,
            @Valid @RequestBody PaisRequestDTO requestDTO) {
        PaisDTO pais = paisService.actualizar(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("País actualizado exitosamente", pais));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un país (soft delete)", description = "Requiere rol ADMIN. Marca el país como inactivo.")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID del país") @PathVariable String id) {
        paisService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("País eliminado exitosamente"));
    }
}
