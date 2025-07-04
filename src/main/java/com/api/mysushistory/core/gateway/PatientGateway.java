package com.api.mysushistory.core.gateway;

import com.api.mysushistory.core.domain.Patient;
import java.util.Optional;

public interface PatientGateway {

  Patient save(final Patient patient);

  Optional<Patient> findByCpf(final String cpf);
}
