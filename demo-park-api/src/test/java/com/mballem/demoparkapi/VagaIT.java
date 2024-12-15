package com.mballem.demoparkapi;
import com.mballem.demoparkapi.web.dto.VagaCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/vagas/vagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/SQL/vagas/vagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class VagaIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void criarVaga_ComDadosValidos_RetornarLocation_Status201() {
        webTestClient
                .post()
                .uri("/api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("A-05", "LIVRE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }
}
