package com.api.mysushistory.presenter;

import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.presenter.response.PatientPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class PatientPresenter {

  public PatientPresenterResponse parseToResponse(final Patient patient) {
    return PatientPresenterResponse.builder()
        .id(patient.getId())
        .name(patient.getName())
        .cpf(patient.getCpf())
        .birthDate(patient.getBirthDate())
        .build();
  }
}
