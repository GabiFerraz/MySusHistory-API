package com.api.mysushistory.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.PatientEntity;
import com.api.mysushistory.infra.persistence.entity.ShareTokenEntity;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import com.api.mysushistory.infra.persistence.repository.ShareTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ShareTokenGatewayImplTest {

  private final PatientRepository patientRepository = mock(PatientRepository.class);
  private final ShareTokenRepository shareTokenRepository = mock(ShareTokenRepository.class);
  private final ShareTokenGatewayImpl gateway =
      new ShareTokenGatewayImpl(patientRepository, shareTokenRepository);

  @Test
  void shouldSaveShareTokenSuccessfully() {
    final var patientId = 1L;
    final var patientEntity =
        PatientEntity.builder()
            .id(patientId)
            .cpf("12345678900")
            .name("Test")
            .birthDate(null)
            .build();
    final var captor = ArgumentCaptor.forClass(ShareTokenEntity.class);
    final var tokenValue = UUID.randomUUID().toString();
    final var now = LocalDateTime.now();
    final var entityResponse =
        ShareTokenEntity.builder()
            .id(100L)
            .accessToken(tokenValue)
            .expiresInMinutes(30)
            .createdAt(now)
            .patient(patientEntity)
            .build();
    final var domainToken = new ShareToken(null, tokenValue, 30, now, patientId);

    when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));
    when(shareTokenRepository.save(captor.capture())).thenReturn(entityResponse);

    final var response = gateway.save(domainToken, patientId);

    assertThat(response.getId()).isEqualTo(100L);
    assertThat(response.getAccessToken()).isEqualTo(tokenValue);
    assertThat(response.getExpiresInMinutes()).isEqualTo(30);
    assertThat(response.getCreatedAt()).isEqualTo(now);
    assertThat(response.getPatientId()).isEqualTo(patientId);

    final var captured = captor.getValue();
    assertThat(captured.getId()).isNull();
    assertThat(captured.getAccessToken()).isEqualTo(tokenValue);
    assertThat(captured.getExpiresInMinutes()).isEqualTo(30);
    assertThat(captured.getCreatedAt()).isEqualTo(now);
    assertThat(captured.getPatient()).isEqualTo(patientEntity);
  }

  @Test
  void shouldThrowGatewayExceptionWhenPatientNotFoundOnSave() {
    final var patientId = 2L;

    when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                gateway.save(
                    new ShareToken(null, "t", 10, LocalDateTime.now(), patientId), patientId))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Patient with ID=[2] not found.");
  }

  @Test
  void shouldThrowGatewayExceptionWhenSaveError() {
    final var patientId = 1L;
    final var patientEntity = PatientEntity.builder().id(patientId).build();

    when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientEntity));
    when(shareTokenRepository.save(any())).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(
            () ->
                gateway.save(
                    new ShareToken(null, "t", 10, LocalDateTime.now(), patientId), patientId))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error saving token for patient_id=[1].");
  }

  @Test
  void shouldFindByAccessTokenSuccessfully() {
    final var tokenValue = "abc";
    final var patientEntity = PatientEntity.builder().id(3L).build();
    final var now = LocalDateTime.now();
    final var entity =
        ShareTokenEntity.builder()
            .id(200L)
            .accessToken(tokenValue)
            .expiresInMinutes(20)
            .createdAt(now)
            .patient(patientEntity)
            .build();

    when(shareTokenRepository.findByAccessToken(tokenValue)).thenReturn(Optional.of(entity));

    final var response = gateway.findByAccessToken(tokenValue);

    assertThat(response).isPresent();

    final var result = response.get();
    assertThat(result.getId()).isEqualTo(200L);
    assertThat(result.getAccessToken()).isEqualTo(tokenValue);
    assertThat(result.getExpiresInMinutes()).isEqualTo(20);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getPatientId()).isEqualTo(3L);
  }

  @Test
  void shouldReturnEmptyWhenFindByAccessTokenNotFound() {
    when(shareTokenRepository.findByAccessToken("nope")).thenReturn(Optional.empty());

    final var response = gateway.findByAccessToken("nope");

    assertThat(response).isEmpty();
  }

  @Test
  void shouldThrowGatewayExceptionWhenFindByAccessTokenError() {
    when(shareTokenRepository.findByAccessToken("err")).thenThrow(new IllegalArgumentException());

    assertThatThrownBy(() -> gateway.findByAccessToken("err"))
        .isInstanceOf(GatewayException.class)
        .hasMessage("Error finding token=[err].");
  }
}
