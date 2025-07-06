package com.api.mysushistory.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.MedicalRecordEntity;
import com.api.mysushistory.infra.persistence.entity.PatientEntity;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PatientGatewayImplTest {

  private final PatientRepository patientRepository = mock(PatientRepository.class);
  private final PatientGatewayImpl gateway = new PatientGatewayImpl(patientRepository);

  @Test
  void shouldSavePatientSuccessfully() {
    final var entityResponse =
        PatientEntity.builder()
            .id(1L)
            .name("John Doe")
            .cpf("12345678901")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();
    final ArgumentCaptor<PatientEntity> patientCaptor =
        ArgumentCaptor.forClass(PatientEntity.class);
    final var patient =
        new Patient(null, "John Doe", "12345678901", LocalDate.of(1990, 1, 1), List.of());
    final var patientEntity =
        PatientEntity.builder()
            .name("John Doe")
            .cpf("12345678901")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

    when(patientRepository.save(patientCaptor.capture())).thenReturn(entityResponse);

    final var response = gateway.save(patient);

    assertThat(response.getId()).isEqualTo(entityResponse.getId());
    assertThat(response.getName()).isEqualTo(entityResponse.getName());
    assertThat(response.getCpf()).isEqualTo(entityResponse.getCpf());
    assertThat(response.getBirthDate()).isEqualTo(entityResponse.getBirthDate());

    final var patientCaptured = patientCaptor.getValue();
    verify(patientRepository).save(patientCaptured);

    assertThat(patientCaptured.getId()).isNull();
    assertThat(patientCaptured.getName()).isEqualTo(patientEntity.getName());
    assertThat(patientCaptured.getCpf()).isEqualTo(patientEntity.getCpf());
    assertThat(patientCaptured.getBirthDate()).isEqualTo(patientEntity.getBirthDate());
  }

  @Test
  void shouldThrowGatewayExceptionWhenSaveError() {
    final var patient =
        new Patient(null, "Jane Doe", "09876543210", LocalDate.of(1985, 5, 5), List.of());

    when(patientRepository.save(any())).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(() -> gateway.save(patient))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error saving patient with CPF=[09876543210].");
  }

  @Test
  void shouldFindByCpfSuccessfully() {
    final var entity =
        PatientEntity.builder()
            .id(2L)
            .name("Alice")
            .cpf("22233344455")
            .birthDate(LocalDate.of(1970, 7, 7))
            .medicalRecords(
                List.of(
                    MedicalRecordEntity.builder()
                        .id(10L)
                        .date(LocalDateTime.of(2023, 1, 1, 12, 0))
                        .unit("UBS X")
                        .professionalName("Dr. X")
                        .diagnosis("Dx")
                        .treatment("Tx")
                        .notes("Note")
                        .patient(null)
                        .build()))
            .build();

    when(patientRepository.findByCpf("22233344455")).thenReturn(Optional.of(entity));

    final var response = gateway.findByCpf("22233344455");

    assertThat(response).isPresent();

    final var patient = response.get();
    assertThat(patient.getId()).isEqualTo(2L);
    assertThat(patient.getName()).isEqualTo("Alice");
    assertThat(patient.getCpf()).isEqualTo("22233344455");
    assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(1970, 7, 7));

    final var records = patient.getMedicalRecords();
    assertThat(records).hasSize(1);

    final var rec = records.get(0);
    assertThat(rec.getId()).isEqualTo(10L);
    assertThat(rec.getUnit()).isEqualTo("UBS X");
  }

  @Test
  void shouldReturnEmptyWhenFindByCpfNotFound() {
    when(patientRepository.findByCpf("00011122233")).thenReturn(Optional.empty());

    final var response = gateway.findByCpf("00011122233");

    assertThat(response).isEmpty();
  }

  @Test
  void shouldThrowGatewayExceptionWhenFindByCpfError() {
    when(patientRepository.findByCpf("00011122233")).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(() -> gateway.findByCpf("00011122233"))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Patient with CPF=[00011122233] not found.");
  }
}
