package com.mballem.demoparkapi.repository;

import com.mballem.demoparkapi.entity.ClienteVaga;
import com.mballem.demoparkapi.repository.projection.ClienteProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteVagaRepository  extends JpaRepository<ClienteVaga, Long> {

    Optional<ClienteVaga> findByReciboAndDataSaidaIsNull(String recibo);

    long countByClienteCpfAndDataSaidaIsNotNull(String cpf);

    Page<ClienteProjection> findAllByClienteCpf(String cpf, Pageable pageable);

    Page<ClienteProjection> findAllByClienteUsuarioId(Long id, Pageable pageable);
}
