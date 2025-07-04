package com.api.mysushistory.presenter.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PatientPresenterResponse(Long id, String cpf, String name, LocalDate birthDate) {}
