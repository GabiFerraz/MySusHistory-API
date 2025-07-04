package com.api.mysushistory.presenter;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.presenter.response.MedicalRecordPresenterResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordPresenter {

  public List<MedicalRecordPresenterResponse> parseToResponseList(
      final List<MedicalRecord> records) {
    return records.stream().map(this::parseToResponse).toList();
  }

  public MedicalRecordPresenterResponse parseToResponse(final MedicalRecord record) {
    return MedicalRecordPresenterResponse.builder()
        .id(record.getId())
        .date(record.getDate())
        .unit(record.getUnit())
        .professionalName(record.getProfessionalName())
        .diagnosis(record.getDiagnosis())
        .treatment(record.getTreatment())
        .notes(record.getNotes())
        .build();
  }
}
