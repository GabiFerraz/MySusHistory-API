package com.api.mysushistory.presenter.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ShareTokenPresenterResponse(
    Long id,
    String accessToken,
    Integer expiresInMinutes,
    LocalDateTime createdAt,
    Long patientId) {}
