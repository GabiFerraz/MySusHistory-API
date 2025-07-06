package com.api.mysushistory.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.mysushistory.core.domain.exception.DomainException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ShareTokenTest {

  @Test
  void shouldCreateShareTokenSuccessfully() {
    LocalDateTime now = LocalDateTime.now();
    String tokenValue = UUID.randomUUID().toString();
    ShareToken token = ShareToken.createShareToken(tokenValue, 30, now, 123L);

    assertThat(token.getAccessToken()).isEqualTo(tokenValue);
    assertThat(token.getExpiresInMinutes()).isEqualTo(30);
    assertThat(token.getCreatedAt()).isEqualTo(now);
    assertThat(token.getPatientId()).isEqualTo(123L);
    assertThat(token.isExpired()).isFalse();
  }

  @Test
  void shouldBeExpiredWhenCreatedAtPlusExpiresInIsBeforeNow() {
    LocalDateTime createdAt = LocalDateTime.now().minusMinutes(60);
    ShareToken token = ShareToken.createShareToken("abc", 30, createdAt, 1L);
    assertThat(token.isExpired()).isTrue();
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotCreateWhenAccessTokenIsBlank(String invalidToken) {
    assertThatThrownBy(() -> ShareToken.createShareToken(invalidToken, 10, LocalDateTime.now(), 1L))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[accessToken] should not be empty or null by domain share token");
  }

  @Test
  void shouldNotCreateWhenExpiresInMinutesIsNull() {
    assertThatThrownBy(() -> ShareToken.createShareToken("token", null, LocalDateTime.now(), 1L))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[expiresInMinutes] should not be empty or null by domain share token");
  }

  @ValueSource(ints = {0, -1})
  @ParameterizedTest
  void shouldNotCreateWhenExpiresInMinutesIsNotPositive(int invalidValue) {
    assertThatThrownBy(
            () -> ShareToken.createShareToken("token", invalidValue, LocalDateTime.now(), 1L))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[expiresInMinutes] must be greater than 0 by domain share token");
  }

  @Test
  void shouldNotCreateWhenCreatedAtIsNull() {
    assertThatThrownBy(() -> ShareToken.createShareToken("token", 10, null, 1L))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[createdAt] should not be empty or null by domain share token");
  }

  @Test
  void shouldNotCreateWhenPatientIdIsNull() {
    assertThatThrownBy(() -> ShareToken.createShareToken("token", 10, LocalDateTime.now(), null))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[patientId] should not be empty or null by domain share token");
  }
}
