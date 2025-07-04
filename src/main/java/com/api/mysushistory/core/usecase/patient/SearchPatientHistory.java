package com.api.mysushistory.core.usecase.patient;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.gateway.MedicalRecordGateway;
import com.api.mysushistory.core.gateway.PatientGateway;
import com.api.mysushistory.core.usecase.exception.PatientNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchPatientHistory {

  private final PatientGateway patientGateway;
  private final MedicalRecordGateway medicalRecordGateway;

  public List<MedicalRecord> execute(final String cpf) {
    final var patient = this.patientGateway.findByCpf(cpf);

    if (patient.isEmpty()) {
      throw new PatientNotFoundException(cpf);
    }

    return this.medicalRecordGateway.findByPatientCpf(cpf);
  }
}
