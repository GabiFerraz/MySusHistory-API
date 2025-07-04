package com.api.mysushistory.core.domain;

import static java.lang.String.format;

import com.api.mysushistory.core.domain.exception.DomainException;
import com.api.mysushistory.core.domain.valueobject.ValidationDomain;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShareToken {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain share token";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String NEGATIVE_NUMBER_ERROR = "Field=[%s] must be greater than 0";

  private Long id;
  private String accessToken;
  private Integer expiresInMinutes;
  private LocalDateTime createdAt;
  private Long patientId;

  public ShareToken() {}

  public ShareToken(
      final Long id,
      final String accessToken,
      final Integer expiresInMinutes,
      final LocalDateTime createdAt,
      final Long patientId) {

    validateDomain(accessToken, expiresInMinutes, createdAt, patientId);

    this.id = id;
    this.accessToken = accessToken;
    this.expiresInMinutes = expiresInMinutes;
    this.createdAt = createdAt;
    this.patientId = patientId;
  }

  public static ShareToken createShareToken(
      final String accessToken,
      final Integer expiresInMinutes,
      final LocalDateTime createdAt,
      final Long patientId) {

    validateDomain(accessToken, expiresInMinutes, createdAt, patientId);

    return new ShareToken(null, accessToken, expiresInMinutes, createdAt, patientId);
  }

  public boolean isExpired() {
    return createdAt.plusMinutes(expiresInMinutes).isBefore(LocalDateTime.now());
  }

  public Long getId() {
    return id;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public Integer getExpiresInMinutes() {
    return expiresInMinutes;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public Long getPatientId() {
    return patientId;
  }

  private static void validateDomain(
      final String accessToken,
      final Integer expiresInMinutes,
      final LocalDateTime createdAt,
      final Long patientId) {

    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                accessToken,
                format(BLANK_MESSAGE_ERROR, "accessToken"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                expiresInMinutes,
                format(BLANK_MESSAGE_ERROR, "expiresInMinutes"),
                List.of(Objects::isNull)),
            new ValidationDomain<>(
                expiresInMinutes,
                format(NEGATIVE_NUMBER_ERROR, "expiresInMinutes"),
                List.of(v -> v != null && v <= 0)),
            new ValidationDomain<>(
                createdAt, format(BLANK_MESSAGE_ERROR, "createdAt"), List.of(Objects::isNull)),
            new ValidationDomain<>(
                patientId, format(BLANK_MESSAGE_ERROR, "patientId"), List.of(Objects::isNull)));

    final var errors = validate(rules);
    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(ShareToken::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(p -> p.test(domain.field()));
  }
}
