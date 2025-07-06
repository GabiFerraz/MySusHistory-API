package com.api.mysushistory.entrypoint.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.usecase.medical.AccessHistoryByToken;
import com.api.mysushistory.core.usecase.medical.RegisterMedicalRecordByToken;
import com.api.mysushistory.presenter.request.RegisterMedicalRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MedicalRecordControllerTest {

  private static final String BASE_URL = "/api/public/medical-records";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AccessHistoryByToken accessHistoryByToken;
  @MockitoBean private RegisterMedicalRecordByToken registerMedicalRecordByToken;

  @Test
  void shouldReturnRecordsWhenTokenValid() throws Exception {
    final var token = UUID.randomUUID().toString();
    final var medicalRecord =
        new MedicalRecord(
            1L,
            LocalDateTime.of(2024, 6, 1, 10, 0),
            "UBS Central",
            "Dra. Ana Lima",
            "Gripe",
            "Repouso e hidratação",
            "Revisar em 5 dias");

    when(accessHistoryByToken.execute(token)).thenReturn(List.of(medicalRecord));

    mockMvc
        .perform(get(BASE_URL).param("token", token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(medicalRecord.getId()))
        .andExpect(jsonPath("$[0].date", startsWith(medicalRecord.getDate().toString())))
        .andExpect(jsonPath("$[0].unit").value(medicalRecord.getUnit()))
        .andExpect(jsonPath("$[0].professionalName").value(medicalRecord.getProfessionalName()))
        .andExpect(jsonPath("$[0].diagnosis").value(medicalRecord.getDiagnosis()))
        .andExpect(jsonPath("$[0].treatment").value(medicalRecord.getTreatment()))
        .andExpect(jsonPath("$[0].notes").value(medicalRecord.getNotes()));
  }

  @Test
  void shouldRegisterRecordWhenTokenValid() throws Exception {
    final var token = UUID.randomUUID().toString();
    final var request =
        new RegisterMedicalRecordRequest(
            "UBS Central", "Dra. Ana Lima", "Gripe", "Repouso e hidratação", "Revisar em 5 dias");
    final var medicalRecord =
        new MedicalRecord(
            1L,
            LocalDateTime.of(2024, 6, 1, 10, 0),
            request.unit(),
            request.professionalName(),
            request.diagnosis(),
            request.treatment(),
            request.notes());

    when(registerMedicalRecordByToken.execute(
            eq(token),
            eq(request.unit()),
            eq(request.professionalName()),
            eq(request.diagnosis()),
            eq(request.treatment()),
            eq(request.notes())))
        .thenReturn(medicalRecord);

    mockMvc
        .perform(
            post(BASE_URL)
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(medicalRecord.getId()))
        .andExpect(jsonPath("$.date", startsWith(medicalRecord.getDate().toString())))
        .andExpect(jsonPath("$.unit").value(medicalRecord.getUnit()))
        .andExpect(jsonPath("$.professionalName").value(medicalRecord.getProfessionalName()))
        .andExpect(jsonPath("$.diagnosis").value(medicalRecord.getDiagnosis()))
        .andExpect(jsonPath("$.treatment").value(medicalRecord.getTreatment()))
        .andExpect(jsonPath("$.notes").value(medicalRecord.getNotes()));
  }
}
