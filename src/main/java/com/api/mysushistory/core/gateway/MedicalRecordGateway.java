package com.api.mysushistory.core.gateway;

import com.api.mysushistory.core.domain.MedicalRecord;
import java.util.List;

public interface MedicalRecordGateway {

  MedicalRecord save(final MedicalRecord medicalRecord, final Long patientId);

  List<MedicalRecord> findByPatientCpf(final String patientCpf);

  List<MedicalRecord> findByPatientId(final Long patientId);
}
