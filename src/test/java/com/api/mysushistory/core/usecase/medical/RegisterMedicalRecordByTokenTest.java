package com.api.mysushistory.core.usecase.medical;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.ShareTokenExpiredException;
import com.api.mysushistory.core.usecase.exception.ShareTokenNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegisterMedicalRecordByTokenTest {

  private final ShareTokenGateway tokenGateway = mock(ShareTokenGateway.class);
  private final MedicalRecordGateway recordGateway = mock(MedicalRecordGateway.class);
  private final RegisterMedicalRecordByToken registerMedicalRecordByToken =
      new RegisterMedicalRecordByToken(tokenGateway, recordGateway);

  @Test
  void shouldRegisterMedicalRecordSuccessfully() {
    final var token = UUID.randomUUID().toString();
    final var patientId = 1L;
    final var shareToken = new ShareToken(1L, token, 60, LocalDateTime.now(), patientId);
    final var unit = "UBS Central";
    final var professionalName = "Dra. Ana Lima";
    final var diagnosis = "Gripe";
    final var treatment = "Analgésico e repouso";
    final var notes = "Reavaliar em 3 dias";
    final var expectedRecord =
        new MedicalRecord(
            10L, LocalDateTime.now(), unit, professionalName, diagnosis, treatment, notes);

    when(tokenGateway.findByAccessToken(token)).thenReturn(Optional.of(shareToken));
    when(recordGateway.save(any(MedicalRecord.class), eq(patientId))).thenReturn(expectedRecord);

    final var response =
        registerMedicalRecordByToken.execute(
            token, unit, professionalName, diagnosis, treatment, notes);

    assertThat(response).isEqualTo(expectedRecord);

    verify(tokenGateway).findByAccessToken(token);
    verify(recordGateway).save(any(MedicalRecord.class), eq(patientId));
  }

  @Test
  void shouldThrowShareTokenNotFoundExceptionWhenTokenIsMissing() {
    final var token = "invalid-token";

    when(tokenGateway.findByAccessToken(token)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                registerMedicalRecordByToken.execute(
                    token, "unit", "name", "diagnosis", "treatment", "notes"))
        .isInstanceOf(ShareTokenNotFoundException.class)
        .hasMessage("Share token [" + token + "] not found.");

    verify(tokenGateway).findByAccessToken(token);
    verifyNoInteractions(recordGateway);
  }

  @Test
  void shouldThrowShareTokenExpiredExceptionWhenTokenIsExpired() {
    final var token = UUID.randomUUID().toString();
    final var expiredToken = new ShareToken(1L, token, 1, LocalDateTime.now().minusMinutes(10), 1L);

    when(tokenGateway.findByAccessToken(token)).thenReturn(Optional.of(expiredToken));

    assertThatThrownBy(
            () ->
                registerMedicalRecordByToken.execute(
                    token, "unit", "name", "diagnosis", "treatment", "notes"))
        .isInstanceOf(ShareTokenExpiredException.class)
        .hasMessage("Share token [" + token + "] has expired.");

    verify(tokenGateway).findByAccessToken(token);
    verifyNoInteractions(recordGateway);
  }
}
