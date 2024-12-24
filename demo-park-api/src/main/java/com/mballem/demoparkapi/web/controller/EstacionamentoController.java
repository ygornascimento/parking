package com.mballem.demoparkapi.web.controller;
import com.mballem.demoparkapi.entity.ClienteVaga;
import com.mballem.demoparkapi.jwt.JwtUserDetails;
import com.mballem.demoparkapi.repository.projection.ClienteProjection;
import com.mballem.demoparkapi.service.ClienteVagaService;
import com.mballem.demoparkapi.service.EstacionamentoService;
import com.mballem.demoparkapi.web.dto.EstacionamentoCreateDTO;
import com.mballem.demoparkapi.web.dto.EstacionamentoResponseDTO;
import com.mballem.demoparkapi.web.dto.PageableDTO;
import com.mballem.demoparkapi.web.dto.mapper.ClienteVagaMapper;
import com.mballem.demoparkapi.web.dto.mapper.PageableMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.PATH;

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

    @Operation(summary = "Localizar um veículo estacionado", description = "Recurso para retornar um veículo estacionado " +
            "pelo nº do recibo. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do rebibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/checkin/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDTO> getByRecibo(@PathVariable String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        EstacionamentoResponseDTO dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Operação de check-out", description = "Recurso para dar saída de um veículo do estacionamento. " +
            "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = { @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do rebibo gerado pelo check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso atualzado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo inexistente ou " +
                            "o veículo já passou pelo check-out.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/checkout/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDTO> checkout(@PathVariable String recibo) {
        ClienteVaga clienteVaga = estacionamentoService.checkout(recibo);
        EstacionamentoResponseDTO dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os " +
            "registros de estacionamentos do cliente por CPF. Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "cpf", description = "Nº do CPF referente ao cliente a ser consultado",
                            required = true
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Representa a página retornada",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Representa o total de elementos por página",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", description = "Campo padrão de ordenação 'dataEntrada,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> getAllEstacionamentosPorCpf(@PathVariable String cpf,
                                                                   @PageableDefault(size = 5, sort = "dataEntrada",
                                                                   direction =Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteProjection> projection = clienteVagaService.buscarTodosPorClienteCpf(cpf, pageable);
        PageableDTO dto = PageableMapper.toDTO(projection);
        return ResponseEntity.ok(dto);

    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente logado",
            description = "Localizar os registros de estacionamentos do cliente logado. " +
                    "Requisição exige uso de um bearer token.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "dataEntrada,asc")),
                            description = "Campo padrão de ordenação 'dataEntrada,asc'. ")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de ADMIN",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PageableDTO> getAllEstacionamentosDoCliente(@AuthenticationPrincipal JwtUserDetails user,
                                                                   @PageableDefault(size = 5, sort = "dataEntrada",
                                                                           direction =Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteProjection> projection = clienteVagaService.buscarTodosPorUsuarioId(user.getId(), pageable);
        PageableDTO dto = PageableMapper.toDTO(projection);
        return ResponseEntity.ok(dto);

    }
}
