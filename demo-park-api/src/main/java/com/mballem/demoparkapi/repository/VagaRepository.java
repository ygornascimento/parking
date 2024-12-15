package com.mballem.demoparkapi.repository;

import com.mballem.demoparkapi.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
}
