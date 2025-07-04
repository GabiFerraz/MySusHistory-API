package com.api.mysushistory.core.usecase.patient;

import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.InvalidTokenExpirationException;
import com.api.mysushistory.core.usecase.exception.PatientNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateToken {

  private final ShareTokenGateway shareTokenGateway;
  private final PatientGateway patientGateway;

  public ShareToken execute(final String patientCpf, final int expiresInMinutes) {

    if (expiresInMinutes <= 0) {
      throw new InvalidTokenExpirationException();
    }

    final var patient =
        this.patientGateway
            .findByCpf(patientCpf)
            .orElseThrow(() -> new PatientNotFoundException(patientCpf));

    final var token =
        ShareToken.createShareToken(
            UUID.randomUUID().toString(), expiresInMinutes, LocalDateTime.now(), patient.getId());

    return this.shareTokenGateway.save(token, patient.getId());
  }
}
