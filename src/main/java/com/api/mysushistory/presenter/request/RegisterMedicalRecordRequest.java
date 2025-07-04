package com.api.mysushistory.presenter.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterMedicalRecordRequest(
    @NotBlank String unit,
    @NotBlank String professionalName,
    @NotBlank String diagnosis,
    @NotBlank String treatment,
    @Size(max = 500) String notes) {}
