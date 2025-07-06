package com.api.mysushistory.core.usecase.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.usecase.exception.PatientNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchPatientHistoryTest {

  private final PatientGateway patientGateway = mock(PatientGateway.class);
  private final MedicalRecordGateway recordGateway = mock(MedicalRecordGateway.class);
  private final SearchPatientHistory searchPatientHistory =
      new SearchPatientHistory(patientGateway, recordGateway);

  @Test
  void shouldReturnMedicalRecordsWhenPatientExists() {
    final var cpf = "12345678900";
    final var patient = new Patient(1L, "John Doe", cpf, LocalDate.of(1990, 1, 1), null);
    final var gatewayResponse =
        List.of(
            new MedicalRecord(
                1L,
                LocalDateTime.of(2022, 1, 1, 0, 0),
                "UBS Jardim das Flores",
                "Dra. Mariana Silva",
                "Hipertensão arterial",
                "Uso contínuo de losartana 50mg",
                "Paciente será reavaliado em 30 dias"));

    when(patientGateway.findByCpf(cpf)).thenReturn(Optional.of(patient));
    when(recordGateway.findByPatientCpf(cpf)).thenReturn(gatewayResponse);

    final var response = searchPatientHistory.execute(cpf);

    assertThat(response).isSameAs(gatewayResponse);
    verify(patientGateway).findByCpf(cpf);
    verify(recordGateway).findByPatientCpf(cpf);
  }

  @Test
  void shouldThrowPatientNotFoundExceptionWhenPatientMissing() {
    final var cpf = "12345678900";

    when(patientGateway.findByCpf(cpf)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> searchPatientHistory.execute(cpf))
        .isInstanceOf(PatientNotFoundException.class)
        .hasMessage("Patient with identifier=[12345678900] not found.");

    verify(patientGateway).findByCpf(cpf);
    verifyNoInteractions(recordGateway);
  }
}
