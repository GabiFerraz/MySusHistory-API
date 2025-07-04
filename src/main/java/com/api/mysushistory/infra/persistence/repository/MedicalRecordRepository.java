package com.api.mysushistory.infra.persistence.repository;

import com.api.mysushistory.infra.persistence.entity.MedicalRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {

  List<MedicalRecordEntity> findByPatientCpf(final String patientCpf);

  List<MedicalRecordEntity> findByPatientId(final Long patientId);
}
