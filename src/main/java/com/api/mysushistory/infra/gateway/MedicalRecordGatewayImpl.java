package com.api.mysushistory.infra.gateway;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.MedicalRecordEntity;
import com.api.mysushistory.infra.persistence.repository.MedicalRecordRepository;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalRecordGatewayImpl implements MedicalRecordGateway {

  private static final String SAVE_ERROR = "Error saving medical record for patientId=[%d].";
  private static final String PATIENT_NOT_FOUND = "PatientEntity with id=[%d] not found.";
  private static final String FIND_BY_CPF_ERROR = "Error finding medical records for cpf=[%s].";
  private static final String FIND_BY_ID_ERROR =
      "Error finding medical records for patientId=[%d].";

  private final MedicalRecordRepository recordRepository;
  private final PatientRepository patientRepository;

  @Override
  public MedicalRecord save(final MedicalRecord medicalRecord, final Long patientId) {
    try {
      final var patientEntity =
          patientRepository
              .findById(patientId)
              .orElseThrow(() -> new GatewayException(String.format(PATIENT_NOT_FOUND, patientId)));

      var entity =
          MedicalRecordEntity.builder()
              .date(medicalRecord.getDate())
              .unit(medicalRecord.getUnit())
              .professionalName(medicalRecord.getProfessionalName())
              .diagnosis(medicalRecord.getDiagnosis())
              .treatment(medicalRecord.getTreatment())
              .notes(medicalRecord.getNotes())
              .patient(patientEntity)
              .build();

      var saved = recordRepository.save(entity);

      return toDomain(saved);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(String.format(SAVE_ERROR, patientId));
    }
  }

  @Override
  public List<MedicalRecord> findByPatientCpf(final String patientCpf) {
    try {
      return recordRepository.findByPatientCpf(patientCpf).stream().map(this::toDomain).toList();
    } catch (IllegalArgumentException e) {
      throw new GatewayException(String.format(FIND_BY_CPF_ERROR, patientCpf));
    }
  }

  @Override
  public List<MedicalRecord> findByPatientId(final Long patientId) {
    try {
      return recordRepository.findByPatientId(patientId).stream().map(this::toDomain).toList();
    } catch (IllegalArgumentException e) {
      throw new GatewayException(String.format(FIND_BY_ID_ERROR, patientId));
    }
  }

  private MedicalRecord toDomain(final MedicalRecordEntity entity) {
    return new MedicalRecord(
        entity.getId(),
        entity.getDate(),
        entity.getUnit(),
        entity.getProfessionalName(),
        entity.getDiagnosis(),
        entity.getTreatment(),
        entity.getNotes());
  }
}
