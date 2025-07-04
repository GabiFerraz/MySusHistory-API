package com.api.mysushistory.infra.gateway;

import static java.lang.String.format;

import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.infra.gateway.exception.GatewayException;
import com.api.mysushistory.infra.persistence.entity.ShareTokenEntity;
import com.api.mysushistory.infra.persistence.repository.PatientRepository;
import com.api.mysushistory.infra.persistence.repository.ShareTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareTokenGatewayImpl implements ShareTokenGateway {

  private static final String SAVE_ERROR_MESSAGE = "Error saving token for patient_id=[%s].";
  private static final String PATIENT_NOT_FOUND = "Patient with ID=[%d] not found.";
  private static final String FIND_ERROR_MESSAGE = "Error finding token=[%s].";

  private final PatientRepository patientRepository;
  private final ShareTokenRepository shareTokenRepository;

  @Override
  public ShareToken save(final ShareToken shareToken, final Long patientId) {
    try {
      final var patient =
          patientRepository
              .findById(patientId)
              .orElseThrow(() -> new GatewayException(format(PATIENT_NOT_FOUND, patientId)));

      final var entity =
          ShareTokenEntity.builder()
              .accessToken(shareToken.getAccessToken())
              .expiresInMinutes(shareToken.getExpiresInMinutes())
              .createdAt(shareToken.getCreatedAt())
              .patient(patient)
              .build();

      final var saved = shareTokenRepository.save(entity);

      return this.toDomain(saved);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(SAVE_ERROR_MESSAGE, patientId));
    }
  }

  @Override
  public Optional<ShareToken> findByAccessToken(final String token) {
    try {
      final var entity = shareTokenRepository.findByAccessToken(token);

      return entity.map(this::toDomain);
    } catch (IllegalArgumentException e) {
      throw new GatewayException(format(FIND_ERROR_MESSAGE, token));
    }
  }

  private ShareToken toDomain(final ShareTokenEntity entity) {
    return new ShareToken(
        entity.getId(),
        entity.getAccessToken(),
        entity.getExpiresInMinutes(),
        entity.getCreatedAt(),
        entity.getPatient().getId());
  }
}
