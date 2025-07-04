package com.api.mysushistory.core.usecase.medical;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.ShareTokenExpiredException;
import com.api.mysushistory.core.usecase.exception.ShareTokenNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessHistoryByToken {

  private final ShareTokenGateway shareTokenGateway;
  private final MedicalRecordGateway medicalRecordGateway;

  public List<MedicalRecord> execute(final String token) {
    final var shareToken =
        this.shareTokenGateway
            .findByAccessToken(token)
            .orElseThrow(() -> new ShareTokenNotFoundException(token));

    if (shareToken.isExpired()) {
      throw new ShareTokenExpiredException(token);
    }

    return this.medicalRecordGateway.findByPatientId(shareToken.getPatientId());
  }
}
