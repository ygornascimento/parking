package com.mballem.demoparkapi.web.controller;

import com.mballem.demoparkapi.entity.Usuario;
import com.mballem.demoparkapi.service.UsuarioService;
import com.mballem.demoparkapi.web.dto.UsuarioCreateDTO;
import com.mballem.demoparkapi.web.dto.UsuarioResponseDTO;
import com.mballem.demoparkapi.web.dto.UsuarioSenhaDTO;
import com.mballem.demoparkapi.web.dto.mapper.UsuarioMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
@Tag(name = "Usuario", description = "Contém todas as informações necessárias referente ao recurso de usuários.")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Operation(summary = "Cria um novo usuário.",
            description = "Recurso para adiconar um novo usuário.",
            responses = {
                    @ApiResponse(responseCode = "201",
                                description = "Recurso criado com sucesso.",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UsuarioResponseDTO.class))),
                    @ApiResponse(responseCode = "409",
                                description = "Usuário e-mail já cadastrado no sistema.",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422",
                                description = "Recurso não processado por dados de entrada inválidos.",
                                content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO createDTO) {
        Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDTO(user));
    }


    @Operation(summary = "Recupera um usuário pelo ID.",
            description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN ou CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Recurso recuperado com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Usuário sem permissão.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Recurso não encontrado.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE') AND #id == authentication.principal.id)")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toDTO(user));
    }


    @Operation(summary = "Atualizar senha.",
            description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN ou CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Senha atualizada com sucesso."),
                    @ApiResponse(responseCode = "400",
                            description = "Senha não confere.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Usuário sem permissão.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(responseCode = "422",
                            description = "Campos inválidos ou mal formatados.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE') AND #id == authentication.principal.id")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UsuarioSenhaDTO dto) {
        usuarioService.editarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha(), dto.getConfirmarSenha());
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Recupera todos os usuários.",
            description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN ou CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Usuários recuperados com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UsuarioResponseDTO.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Usuário sem permissão.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Usuários não encontrados.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.tolistDTO(users));
    }
}
