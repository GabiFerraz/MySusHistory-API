package com.api.mysushistory.presenter.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MedicalRecordPresenterResponse(
    Long id,
    LocalDateTime date,
    String unit,
    String professionalName,
    String diagnosis,
    String treatment,
    String notes) {}
