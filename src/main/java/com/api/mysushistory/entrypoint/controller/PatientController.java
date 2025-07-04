package com.api.mysushistory.entrypoint.controller;

import com.api.mysushistory.core.usecase.patient.CreatePatient;
import com.api.mysushistory.core.usecase.patient.GenerateToken;
import com.api.mysushistory.core.usecase.patient.SearchPatientHistory;
import com.api.mysushistory.presenter.MedicalRecordPresenter;
import com.api.mysushistory.presenter.PatientPresenter;
import com.api.mysushistory.presenter.ShareTokenPresenter;
import com.api.mysushistory.presenter.response.MedicalRecordPresenterResponse;
import com.api.mysushistory.presenter.response.PatientPresenterResponse;
import com.api.mysushistory.presenter.response.ShareTokenPresenterResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

  private final CreatePatient createPatient;
  private final GenerateToken generateToken;
  private final SearchPatientHistory searchPatientHistory;

  private final PatientPresenter patientPresenter;
  private final ShareTokenPresenter tokenPresenter;
  private final MedicalRecordPresenter medicalRecordPresenter;

  @PostMapping
  public ResponseEntity<PatientPresenterResponse> create(
      @Validated @RequestParam String name,
      @Validated @RequestParam String cpf,
      @Validated @RequestParam LocalDate birthDate) {
    final var patient = this.createPatient.execute(name, cpf, birthDate);

    return new ResponseEntity<>(this.patientPresenter.parseToResponse(patient), HttpStatus.CREATED);
  }

  @PostMapping("/{cpf}/token")
  public ResponseEntity<ShareTokenPresenterResponse> generateToken(
      @PathVariable String cpf, @RequestParam Integer expiresInMinutes) {

    final var token = this.generateToken.execute(cpf, expiresInMinutes);

    return new ResponseEntity<>(this.tokenPresenter.parseToResponse(token), HttpStatus.CREATED);
  }

  @GetMapping("/{cpf}/history")
  public ResponseEntity<List<MedicalRecordPresenterResponse>> getHistory(@PathVariable String cpf) {
    final var histories = this.searchPatientHistory.execute(cpf);

    return ResponseEntity.ok(this.medicalRecordPresenter.parseToResponseList(histories));
  }
}
