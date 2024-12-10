package com.mballem.demoparkapi.web.controller;

import com.mballem.demoparkapi.entity.Cliente;
import com.mballem.demoparkapi.jwt.JwtUserDetails;
import com.mballem.demoparkapi.service.ClienteService;
import com.mballem.demoparkapi.service.UsuarioService;
import com.mballem.demoparkapi.web.dto.ClienteCreateDTO;
import com.mballem.demoparkapi.web.dto.ClienteResponseDTO;
import com.mballem.demoparkapi.web.dto.UsuarioResponseDTO;
import com.mballem.demoparkapi.web.dto.mapper.ClienteMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Cria um novo cliente.",
            description = "Recurso para criar um novo cliente vinculado ao um usuário cadastrado. " +
            "Requisição exige uso de um Bearer Token. Acesso restrito a Role \"CLIENTE\" ",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "Recurso criado com sucesso.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "409",
                            description = "Cliente CPF já cadastrado no sistema.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422",
                            description = "Recurso não processado por falta dados de entrada inválidos.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Acesso não permitido ao perfil ADMIN.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody @Valid ClienteCreateDTO dto, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.save(cliente);

        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Cria um novo cliente.",
            description = "Recurso para criar um novo cliente vinculado ao um usuário cadastrado. " +
                    "Requisição exige uso de um Bearer Token. Acesso restrito a Role \"CLIENTE\" ",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Recurso localizado com sucesso.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Cliente não encontrado no sistema.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Acesso não permitido ao perfil CLIENTE.",
                            content = @Content(mediaType = "application/json;charset=UTF=8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Cliente>> getAll(Pageable pageable) {
        Page<Cliente> clientes = clienteService.buscarTodos(pageable);
        return ResponseEntity.ok(clientes);
    }
}
