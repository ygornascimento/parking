package com.mballem.demoparkapi.web.controller;

import com.mballem.demoparkapi.entity.Vaga;
import com.mballem.demoparkapi.service.VagaService;
import com.mballem.demoparkapi.web.dto.UsuarioResponseDTO;
import com.mballem.demoparkapi.web.dto.VagaCreateDTO;
import com.mballem.demoparkapi.web.dto.VagaResponseDTO;
import com.mballem.demoparkapi.web.dto.mapper.VagaMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/vagas")
public class VagaController {
    private final VagaService vagaService;

    @Operation(summary = "Cria uma nova vaga.",
            description = "Recurso para criar uma nova vaga.",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Recurso criado com sucesso.",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")),
                    @ApiResponse(responseCode = "409",
                            description = "Vaga já cadastrada.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422",
                            description = "Recurso não processado por falta de dados ou dados inválidos.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid VagaCreateDTO dto) {
    Vaga vaga = VagaMapper.toVaga(dto);
    vagaService.salvar(vaga);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{codigo}")
            .buildAndExpand(vaga.getCodigo())
            .toUri();

    return ResponseEntity.created(location).build();
    }


    @Operation(summary = "Localizar uma vaga.",
            description = "Recurso para retornar uma nova vaga pelo seu código.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Recurso localizado com sucesso.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = VagaResponseDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Vaga já cadastrada.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDTO> getByCodigo(@PathVariable String codigo) {
        Vaga vaga = vagaService.buscarPorCodigo(codigo);

        return ResponseEntity.ok(VagaMapper.toDto(vaga));
    }
}
