package com.api.mysushistory.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.MedicalRecordEntity;
import com.api.mysushistory.infra.persistence.entity.PatientEntity;
import com.api.mysushistory.infra.persistence.repository.MedicalRecordRepository;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class MedicalRecordGatewayImplTest {

  private final MedicalRecordRepository recordRepository = mock(MedicalRecordRepository.class);
  private final PatientRepository patientRepository = mock(PatientRepository.class);
  private final MedicalRecordGatewayImpl gateway =
      new MedicalRecordGatewayImpl(recordRepository, patientRepository);

  @Test
  void shouldSaveMedicalRecordSuccessfully() {
    final var patientId = 5L;
    final var patientEntity =
        PatientEntity.builder()
            .id(patientId)
            .cpf("12345678900")
            .name("Test")
            .birthDate(null)
            .build();
    final var medicalRecordEntityCaptor = ArgumentCaptor.forClass(MedicalRecordEntity.class);
    final var domainRecord =
        new MedicalRecord(
            5L, LocalDateTime.of(2024, 1, 1, 9, 0), "UBS A", "Dr. Teste", "Dx", "Tx", "Notes");
    final var entityResponse =
        MedicalRecordEntity.builder()
            .id(10L)
            .date(domainRecord.getDate())
            .unit(domainRecord.getUnit())
            .professionalName(domainRecord.getProfessionalName())
            .diagnosis(domainRecord.getDiagnosis())
            .treatment(domainRecord.getTreatment())
            .notes(domainRecord.getNotes())
            .patient(patientEntity)
            .build();

    when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));
    when(recordRepository.save(medicalRecordEntityCaptor.capture())).thenReturn(entityResponse);

    final var response = gateway.save(domainRecord, patientId);

    assertThat(response.getId()).isEqualTo(entityResponse.getId());
    assertThat(response.getDate()).isEqualTo(domainRecord.getDate());
    assertThat(response.getUnit()).isEqualTo(domainRecord.getUnit());

    final var captured = medicalRecordEntityCaptor.getValue();
    assertThat(captured.getId()).isNull();
    assertThat(captured.getDate()).isEqualTo(domainRecord.getDate());
    assertThat(captured.getUnit()).isEqualTo(domainRecord.getUnit());
    assertThat(captured.getProfessionalName()).isEqualTo(domainRecord.getProfessionalName());
    assertThat(captured.getDiagnosis()).isEqualTo(domainRecord.getDiagnosis());
    assertThat(captured.getTreatment()).isEqualTo(domainRecord.getTreatment());
    assertThat(captured.getNotes()).isEqualTo(domainRecord.getNotes());
    assertThat(captured.getPatient()).isEqualTo(patientEntity);
  }

  @Test
  void shouldThrowGatewayExceptionWhenPatientNotFoundOnSave() {
    final var patientId = 99L;

    when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                gateway.save(
                    MedicalRecord.createMedicalRecord(LocalDateTime.now(), "u", "p", "d", "t", "n"),
                    patientId))
        .isInstanceOf(GatewayException.class)
        .hasMessage("PatientEntity with id=[99] not found.");
  }

  @Test
  void shouldThrowGatewayExceptionWhenSaveError() {
    final var patientId = 5L;
    final var patientEntity = PatientEntity.builder().id(patientId).build();

    when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));
    when(recordRepository.save(any())).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(
            () ->
                gateway.save(
                    MedicalRecord.createMedicalRecord(LocalDateTime.now(), "u", "p", "d", "t", "n"),
                    patientId))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error saving medical record for patientId=[5].");
  }

  @Test
  void shouldFindByPatientCpfSuccessfully() {
    final var cpf = "123";
    final var entity =
        MedicalRecordEntity.builder()
            .id(1L)
            .date(LocalDateTime.now())
            .unit("u")
            .professionalName("p")
            .diagnosis("d")
            .treatment("t")
            .notes("n")
            .build();

    when(recordRepository.findByPatientCpf(cpf)).thenReturn(List.of(entity));

    final var response = gateway.findByPatientCpf(cpf);

    assertThat(response).hasSize(1);
    assertThat(response.get(0).getId()).isEqualTo(entity.getId());
    assertThat(response.get(0).getDate()).isEqualTo(entity.getDate());
    assertThat(response.get(0).getUnit()).isEqualTo(entity.getUnit());
    assertThat(response.get(0).getProfessionalName()).isEqualTo(entity.getProfessionalName());
    assertThat(response.get(0).getDiagnosis()).isEqualTo(entity.getDiagnosis());
    assertThat(response.get(0).getTreatment()).isEqualTo(entity.getTreatment());
    assertThat(response.get(0).getNotes()).isEqualTo(entity.getNotes());
  }

  @Test
  void shouldThrowGatewayExceptionWhenFindByCpfError() {
    when(recordRepository.findByPatientCpf("x")).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(() -> gateway.findByPatientCpf("x"))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error finding medical records for cpf=[x].");
  }

  @Test
  void shouldFindByPatientIdSuccessfully() {
    final var patientId = 7L;
    final var entity =
        MedicalRecordEntity.builder()
            .id(2L)
            .date(LocalDateTime.now())
            .unit("u")
            .professionalName("p")
            .diagnosis("d")
            .treatment("t")
            .notes("n")
            .build();

    when(recordRepository.findByPatientId(patientId)).thenReturn(List.of(entity));

    final var response = gateway.findByPatientId(patientId);

    assertThat(response).hasSize(1);
    assertThat(response.get(0).getId()).isEqualTo(entity.getId());
    assertThat(response.get(0).getDate()).isEqualTo(entity.getDate());
    assertThat(response.get(0).getUnit()).isEqualTo(entity.getUnit());
    assertThat(response.get(0).getProfessionalName()).isEqualTo(entity.getProfessionalName());
    assertThat(response.get(0).getDiagnosis()).isEqualTo(entity.getDiagnosis());
    assertThat(response.get(0).getTreatment()).isEqualTo(entity.getTreatment());
    assertThat(response.get(0).getNotes()).isEqualTo(entity.getNotes());
  }

  @Test
  void shouldThrowGatewayExceptionWhenFindByIdError() {
    when(recordRepository.findByPatientId(8L)).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(() -> gateway.findByPatientId(8L))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error finding medical records for patientId=[8].");
  }
}
