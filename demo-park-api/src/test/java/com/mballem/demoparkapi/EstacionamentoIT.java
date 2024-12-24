package com.mballem.demoparkapi;
import com.mballem.demoparkapi.web.dto.EstacionamentoCreateDTO;
import com.mballem.demoparkapi.web.dto.PageableDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/SQL/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void criarCheckIn_ComDadosValidos_RetornarCreatedAndLocation() {
        EstacionamentoCreateDTO createDTO = EstacionamentoCreateDTO.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("AZUL")
                .clienteCpf("98401203015")
                .build();

        webTestClient.post()
                .uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("WER-1111")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO 1.0")
                .jsonPath("cor").isEqualTo("AZUL")
                .jsonPath("clienteCpf").isEqualTo("98401203015")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void criarCheckIn_ComRoleCliente_RetornarError_Status403() {
        EstacionamentoCreateDTO createDTO = EstacionamentoCreateDTO.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("AZUL")
                .clienteCpf("98401203015")
                .build();

        webTestClient.post()
                .uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bia@email.com.br", "123456"))
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkin")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckIn_ComDadosInvalidos_RetornarError_Status422() {
        EstacionamentoCreateDTO createDTO = EstacionamentoCreateDTO.builder()
                .placa("")
                .marca("")
                .modelo("")
                .cor("")
                .clienteCpf("")
                .build();

        webTestClient.post()
                .uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bia@email.com.br", "123456"))
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkin")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckIn_ComCPFInexistente_RetornarError_Status404() {
        EstacionamentoCreateDTO createDTO = EstacionamentoCreateDTO.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("AZUL")
                .clienteCpf("73287837028")
                .build();

        webTestClient.post()
                .uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkin")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    @Sql(scripts = "/SQL/estacionamentos/estacionamento-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/SQL/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void criarCheckIn_ComVagasOcupadas_RetornarError_Status404() {
        EstacionamentoCreateDTO createDTO = EstacionamentoCreateDTO.builder()
                .placa("WER-1111")
                .marca("FIAT")
                .modelo("PALIO 1.0")
                .cor("AZUL")
                .clienteCpf("98401203015")
                .build();

        webTestClient.post()
                .uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkin")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void buscarCheckIn_ComPerfilAdmin_Retornar_Status200() {

        webTestClient.get()
                .uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("98401203015")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void buscarCheckIn_ComPerfilCliente_Retornar_Status200() {

        webTestClient.get()
                .uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("98401203015")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void buscarCheckIn_ComReciboInexistente_RetornarError_Status404() {

        webTestClient.get()
                .uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkin/20230313-000000")
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void criarCheckOut_ComReciboExistente_RetornarSucesso_Status200() {

        webTestClient.put()
                .uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("dataEntrada").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("clienteCpf").isEqualTo("98401203015")
                .jsonPath("vagaCodigo").isEqualTo("A-01")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataSaida").exists()
                .jsonPath("valor").exists()
                .jsonPath("desconto").exists();
    }

    @Test
    public void criarCheckOut_ComRoleCliente_RetornarErro_Status403() {

        webTestClient.put()
                .uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bia@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkout/20230313-101300")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void criarCheckOut_ComReciboInexistente_RetornarErro_Status404() {

        webTestClient.put()
                .uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/checkout/20230313-000000")
                .jsonPath("method").isEqualTo("PUT");
    }

    @Test
    public void buscarEstacionamentos_PorClienteCpf_RetornarSucesso() {

        PageableDTO responseBody = webTestClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=0", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(0);
        assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = webTestClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=1", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getContent().size()).isEqualTo(1);
        assertThat(responseBody.getTotalPages()).isEqualTo(2);
        assertThat(responseBody.getNumber()).isEqualTo(1);
        assertThat(responseBody.getSize()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamentos_PorClienteCpfComRoleCliente_RetornarErro_Status403() {

        webTestClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}", "98401203015")
                .headers(JwtAuthentication.getHeaderAuthorization(webTestClient, "bia@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/cpf/98401203015")
                .jsonPath("method").isEqualTo("GET");
    }
}
