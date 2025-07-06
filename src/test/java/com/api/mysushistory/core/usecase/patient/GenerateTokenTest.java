package com.api.mysushistory.core.usecase.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.InvalidTokenExpirationException;
import com.api.mysushistory.core.usecase.exception.PatientNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GenerateTokenTest {

  private final ShareTokenGateway tokenGateway = mock(ShareTokenGateway.class);
  private final PatientGateway patientGateway = mock(PatientGateway.class);
  private final GenerateToken generateToken = new GenerateToken(tokenGateway, patientGateway);

  @Test
  void shouldGenerateTokenSuccessfully() {
    final var patient = new Patient(1L, "John Doe", "12345678900", LocalDate.of(1990, 1, 1), null);
    final ArgumentCaptor<ShareToken> tokenCaptor = ArgumentCaptor.forClass(ShareToken.class);
    var gatewayResponse = new ShareToken(10L, "abc-token", 15, LocalDateTime.now(), 1L);

    when(patientGateway.findByCpf("12345678900")).thenReturn(Optional.of(patient));
    when(tokenGateway.save(tokenCaptor.capture(), eq(1L))).thenReturn(gatewayResponse);

    var response = generateToken.execute("12345678900", 15);

    assertThat(response).isEqualTo(gatewayResponse);

    verify(patientGateway).findByCpf("12345678900");

    var tokenCaptured = tokenCaptor.getValue();
    verify(tokenGateway).save(tokenCaptured, 1L);

    assertThat(tokenCaptured.getId()).isNull();
    assertThat(tokenCaptured.getAccessToken()).isNotBlank();
    assertThat(tokenCaptured.getExpiresInMinutes()).isEqualTo(15);
    assertThat(tokenCaptured.getCreatedAt()).isBefore(LocalDateTime.now());
    assertThat(tokenCaptured.getPatientId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowInvalidTokenExpirationExceptionWhenNonPositive() {
    assertThatThrownBy(() -> generateToken.execute("12345678900", 0))
        .isInstanceOf(InvalidTokenExpirationException.class)
        .hasMessage("Expiration time must be greater than zero.");

    verifyNoInteractions(patientGateway, tokenGateway);
  }

  @Test
  void shouldThrowPatientNotFoundExceptionWhenPatientMissing() {
    when(patientGateway.findByCpf("12345678900")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> generateToken.execute("12345678900", 10))
        .isInstanceOf(PatientNotFoundException.class)
        .hasMessage("Patient with identifier=[12345678900] not found.");

    verify(patientGateway).findByCpf("12345678900");
    verifyNoMoreInteractions(tokenGateway);
  }
}
