package com.mballem.demoparkapi.web.controller;
import com.mballem.demoparkapi.entity.ClienteVaga;
import com.mballem.demoparkapi.service.ClienteVagaService;
import com.mballem.demoparkapi.service.EstacionamentoService;
import com.mballem.demoparkapi.web.dto.EstacionamentoCreateDTO;
import com.mballem.demoparkapi.web.dto.EstacionamentoResponseDTO;
import com.mballem.demoparkapi.web.dto.mapper.ClienteVagaMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("api/v1/estacionamentos")
@Tag(name = "Estacionamento", description = "Contém todas as informações necessárias referente ao recurso de estacionamento.")
public class EstacionamentoController {
    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;


    @Operation(summary = "Operação de Check-In.",
            description = "Recurso para dar entrada de um veículo no estacionamento. " + "Requisiçao exige uso de um bearer token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso.",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado."),
                            content = @Content(mediaType = "application/json;charset-UTF-8",
                            schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE.",
                            content = @Content(mediaType = "application/json;charset-UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Causas possíveis: <br/>" +
                            " - CPF do cliente não cadastrado no sistema; <br/>" +
                            " - Nenhuma vaga live foi localizada.",
                            content = @Content(mediaType = "application/json;charset-UTF-8",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422",
                            description = "Campos inválidos ou mal formatados.",
                            content = @Content(mediaType = "application/json;charset-UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> checkin(@RequestBody @Valid EstacionamentoCreateDTO dto) {
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(dto);
        estacionamentoService.checkIn(clienteVaga);

        EstacionamentoResponseDTO responseDTO = ClienteVagaMapper.toDto(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/checkin/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDTO> getByRecibo(@PathVariable String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        EstacionamentoResponseDTO dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }
}
