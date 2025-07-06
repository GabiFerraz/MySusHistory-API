package com.api.mysushistory.core.usecase.patient;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.usecase.exception.PatientAlreadyExistsException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreatePatientTest {

  private final PatientGateway gateway = mock(PatientGateway.class);
  private final CreatePatient createPatient = new CreatePatient(gateway);

  @Test
  void shouldCreatePatientSuccessfully() {
    final var gatewayResponse =
        new Patient(1L, "John Doe", "12345678900", LocalDate.of(1990, 1, 1), null);
    final ArgumentCaptor<Patient> patientCaptor = ArgumentCaptor.forClass(Patient.class);

    when(gateway.findByCpf("12345678900")).thenReturn(empty());
    when(gateway.save(patientCaptor.capture())).thenReturn(gatewayResponse);

    final var response =
        this.createPatient.execute("John Doe", "12345678900", LocalDate.of(1990, 1, 1));

    assertThat(response).isEqualTo(gatewayResponse);

    verify(gateway).findByCpf("12345678900");

    final var patientCaptured = patientCaptor.getValue();
    verify(gateway).save(patientCaptured);

    assertThat(patientCaptured.getId()).isNull();
    assertThat(patientCaptured.getName()).isEqualTo("John Doe");
    assertThat(patientCaptured.getCpf()).isEqualTo("12345678900");
    assertThat(patientCaptured.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    assertThat(patientCaptured.getMedicalRecords()).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenPatientAlreadyExists() {
    final var gatewayResponse =
        new Patient(1L, "John Doe", "12345678900", LocalDate.of(1990, 1, 1), null);

    when(gateway.findByCpf("12345678900")).thenReturn(Optional.of(gatewayResponse));

    assertThatThrownBy(
            () -> this.createPatient.execute("John Doe", "12345678900", LocalDate.of(1990, 1, 1)))
        .isInstanceOf(PatientAlreadyExistsException.class)
        .hasMessage("Patient with cpf=[12345678900] already exists.");

    verify(gateway).findByCpf("12345678900");
    verifyNoMoreInteractions(gateway);
  }
}
