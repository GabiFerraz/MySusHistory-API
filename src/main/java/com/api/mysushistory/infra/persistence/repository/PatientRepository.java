package com.api.mysushistory.infra.persistence.repository;

import com.api.mysushistory.infra.persistence.entity.PatientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

  Optional<PatientEntity> findByCpf(final String cpf);
}
