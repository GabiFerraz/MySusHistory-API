package com.api.mysushistory.core.usecase.patient;

import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.usecase.exception.PatientAlreadyExistsException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePatient {

  private final PatientGateway gateway;

  public Patient execute(final String name, final String cpf, final LocalDate birthDate) {
    final var patient = this.gateway.findByCpf(cpf);

    if (patient.isPresent()) {
      throw new PatientAlreadyExistsException(cpf);
    }

    final var buildDomain = Patient.createPatient(name, cpf, birthDate);

    return this.gateway.save(buildDomain);
  }
}
