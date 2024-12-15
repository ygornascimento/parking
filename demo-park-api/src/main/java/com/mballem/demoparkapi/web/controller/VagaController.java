package com.mballem.demoparkapi.web.controller;

import com.mballem.demoparkapi.entity.Vaga;
import com.mballem.demoparkapi.service.VagaService;
import com.mballem.demoparkapi.web.dto.VagaCreateDTO;
import com.mballem.demoparkapi.web.dto.VagaResponseDTO;
import com.mballem.demoparkapi.web.dto.mapper.VagaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDTO> getByCodigo(@PathVariable String codigo) {
        Vaga vaga = vagaService.buscarPorCodigo(codigo);

        return ResponseEntity.ok(VagaMapper.toDto(vaga));
    }
}
