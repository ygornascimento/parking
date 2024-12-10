package com.mballem.demoparkapi;
import com.mballem.demoparkapi.web.dto.ClienteCreateDTO;
import com.mballem.demoparkapi.web.dto.ClienteResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/clientes/clientes-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/SQL/clientes/clientes-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void criarCliente_ComDadosValidos_RetornarCliente_ComStatus_201() {
        ClienteResponseDTO responseBody = webTestClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("Tobias Ferreira", "68222388088"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getNome()).isEqualTo("Tobias Ferreira");
        assertThat(responseBody.getCpf()).isEqualTo("68222388088");
    }
}
