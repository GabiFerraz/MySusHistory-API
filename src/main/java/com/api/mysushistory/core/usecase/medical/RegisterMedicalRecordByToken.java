package com.api.mysushistory.core.usecase.medical;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.ShareTokenGateway;
import com.api.mysushistory.core.usecase.exception.ShareTokenExpiredException;
import com.api.mysushistory.core.usecase.exception.ShareTokenNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterMedicalRecordByToken {

  private final ShareTokenGateway shareTokenGateway;
  private final MedicalRecordGateway medicalRecordGateway;

  public MedicalRecord execute(
      final String token,
      final String unit,
      final String professionalName,
      final String diagnosis,
      final String treatment,
      final String notes) {

    final var shareToken =
        this.shareTokenGateway
            .findByAccessToken(token)
            .orElseThrow(() -> new ShareTokenNotFoundException(token));

    if (shareToken.isExpired()) {
      throw new ShareTokenExpiredException(token);
    }

    final var medicalRecord =
        MedicalRecord.createMedicalRecord(
            LocalDateTime.now(), unit, professionalName, diagnosis, treatment, notes);

    return this.medicalRecordGateway.save(medicalRecord, shareToken.getPatientId());
  }
}
