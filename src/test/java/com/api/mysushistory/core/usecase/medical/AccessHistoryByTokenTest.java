package com.api.mysushistory.core.usecase.medical;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.ShareTokenExpiredException;
import com.api.mysushistory.core.usecase.exception.ShareTokenNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccessHistoryByTokenTest {

  private final ShareTokenGateway tokenGateway = mock(ShareTokenGateway.class);
  private final MedicalRecordGateway recordGateway = mock(MedicalRecordGateway.class);
  private final AccessHistoryByToken accessHistoryByToken =
      new AccessHistoryByToken(tokenGateway, recordGateway);

  @Test
  void shouldReturnRecordsWhenTokenValid() {
    final var tokenValue = UUID.randomUUID().toString();
    final var shareToken = new ShareToken(1L, tokenValue, 60, LocalDateTime.now(), 42L);
    final var records =
        List.of(
            new MedicalRecord(
                10L,
                LocalDateTime.of(2023, 3, 1, 9, 0),
                "UBS Centro",
                "Dr. Fulano",
                "Resfriado",
                "Repouso",
                "Sem complicações"));

    when(tokenGateway.findByAccessToken(tokenValue)).thenReturn(Optional.of(shareToken));
    when(recordGateway.findByPatientId(42L)).thenReturn(records);

    final var response = accessHistoryByToken.execute(tokenValue);

    assertThat(response).isSameAs(records);

    verify(tokenGateway).findByAccessToken(tokenValue);
    verify(recordGateway).findByPatientId(42L);
  }

  @Test
  void shouldThrowNotFoundWhenTokenMissing() {
    final var tokenValue = "nonexistent";

    when(tokenGateway.findByAccessToken(tokenValue)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accessHistoryByToken.execute(tokenValue))
        .isInstanceOf(ShareTokenNotFoundException.class)
        .hasMessage("Share token [" + tokenValue + "] not found.");

    verify(tokenGateway).findByAccessToken(tokenValue);
    verifyNoInteractions(recordGateway);
  }

  @Test
  void shouldThrowExpiredWhenTokenIsExpired() {
    final var tokenValue = UUID.randomUUID().toString();
    final var expiredToken =
        new ShareToken(1L, tokenValue, 1, LocalDateTime.now().minusMinutes(10), 42L);

    when(tokenGateway.findByAccessToken(tokenValue)).thenReturn(Optional.of(expiredToken));

    assertThatThrownBy(() -> accessHistoryByToken.execute(tokenValue))
        .isInstanceOf(ShareTokenExpiredException.class)
        .hasMessage("Share token [" + tokenValue + "] has expired.");

    verify(tokenGateway).findByAccessToken(tokenValue);
    verifyNoInteractions(recordGateway);
  }
}
