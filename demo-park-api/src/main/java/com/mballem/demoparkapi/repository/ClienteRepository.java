package com.mballem.demoparkapi.repository;

import com.mballem.demoparkapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
