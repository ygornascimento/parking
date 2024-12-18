package com.mballem.demoparkapi.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EstacionamentoService {

    private final ClienteService CLIENTESERVICE;
    private final ClienteVagaService CLIENTEVAGASERVICE;
    private final VagaService VAGASERVICE;

}
