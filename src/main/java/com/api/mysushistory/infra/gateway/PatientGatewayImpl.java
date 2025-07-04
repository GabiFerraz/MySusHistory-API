package com.api.mysushistory.infra.gateway;

import static java.lang.String.format;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.MedicalRecordEntity;
import com.api.mysushistory.infra.persistence.entity.PatientEntity;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientGatewayImpl implements PatientGateway {

  private static final String SAVE_ERROR_MESSAGE = "Error saving patient with CPF=[%s].";
  private static final String FIND_ERROR_MESSAGE = "Patient with CPF=[%s] not found.";

  private final PatientRepository patientRepository;

  @Override
  public Patient save(final Patient patient) {
    try {
      final var entity =
          PatientEntity.builder()
              .name(patient.getName())
              .cpf(patient.getCpf())
              .birthDate(patient.getBirthDate())
              .build();

      final var saved = patientRepository.save(entity);

      return this.toDomain(saved);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(SAVE_ERROR_MESSAGE, patient.getCpf()));
    }
  }

  @Override
  public Optional<Patient> findByCpf(final String cpf) {
    try {
      final var entity = patientRepository.findByCpf(cpf);

      return entity.map(this::toDomain);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(FIND_ERROR_MESSAGE, cpf));
    }
  }

  private Patient toDomain(final PatientEntity entity) {
    return new Patient(
        entity.getId(),
        entity.getName(),
        entity.getCpf(),
        entity.getBirthDate(),
        this.toMedicalRecords(entity.getMedicalRecords()));
  }

  private List<MedicalRecord> toMedicalRecords(final List<MedicalRecordEntity> medicalRecords) {
    return medicalRecords.stream()
        .map(
            it ->
                new MedicalRecord(
                    it.getId(),
                    it.getDate(),
                    it.getUnit(),
                    it.getProfessionalName(),
                    it.getDiagnosis(),
                    it.getTreatment(),
                    it.getNotes()))
        .toList();
  }
}
