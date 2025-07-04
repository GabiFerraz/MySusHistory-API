package com.api.mysushistory.entrypoint.controller;

import com.api.mysushistory.core.usecase.medical.AccessHistoryByToken;
import com.api.mysushistory.core.usecase.medical.RegisterMedicalRecordByToken;
import com.api.mysushistory.presenter.MedicalRecordPresenter;
import com.api.mysushistory.presenter.request.RegisterMedicalRecordRequest;
import com.api.mysushistory.presenter.response.MedicalRecordPresenterResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/medical-records")
public class MedicalRecordController {

  private final AccessHistoryByToken accessHistoryByToken;
  private final RegisterMedicalRecordByToken registerMedicalRecordByToken;
  private final MedicalRecordPresenter presenter;

  @GetMapping
  public ResponseEntity<List<MedicalRecordPresenterResponse>> getByToken(
      @Validated @RequestParam("token") final String token) {

    final var records = this.accessHistoryByToken.execute(token);

    return ResponseEntity.ok(this.presenter.parseToResponseList(records));
  }

  @PostMapping
  public ResponseEntity<MedicalRecordPresenterResponse> registerByToken(
      @Validated @RequestParam("token") final String token,
      @Valid @RequestBody RegisterMedicalRecordRequest request) {

    final var savedRecord =
        this.registerMedicalRecordByToken.execute(
            token,
            request.unit(),
            request.professionalName(),
            request.diagnosis(),
            request.treatment(),
            request.notes());

    return new ResponseEntity<>(this.presenter.parseToResponse(savedRecord), HttpStatus.CREATED);
  }
}
