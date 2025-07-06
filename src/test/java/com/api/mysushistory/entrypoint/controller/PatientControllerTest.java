package com.api.mysushistory.entrypoint.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.mysushistory.core.domain.MedicalRecord;
import com.api.mysushistory.core.domain.Patient;
import com.api.mysushistory.core.domain.ShareToken;
import com.api.mysushistory.core.usecase.patient.CreatePatient;
import com.api.mysushistory.core.usecase.patient.GenerateToken;
import com.api.mysushistory.core.usecase.patient.SearchPatientHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
class PatientControllerTest {

  private static final String BASE_URL = "/api/patients";
  private static final String BASE_URL_CPF_TOKEN = BASE_URL + "/%s/token";
  private static final String BASE_URL_CPF_HISTORY = BASE_URL + "/%s/history";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreatePatient createPatient;
  @MockitoBean private GenerateToken generateToken;
  @MockitoBean private SearchPatientHistory searchPatientHistory;

  @Test
  void shouldCreatePatientSuccessfully() throws Exception {
    final var response =
        new Patient(1L, "John Doe", "12345678900", LocalDate.of(1990, 1, 1), List.of());

    when(this.createPatient.execute("John Doe", "12345678900", LocalDate.of(1990, 1, 1)))
        .thenReturn(response);

    mockMvc
        .perform(
            post(BASE_URL)
                .param("name", "John Doe")
                .param("cpf", "12345678900")
                .param("birthDate", "1990-01-01")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.cpf").value("12345678900"))
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
  }

  @Test
  void shouldGenerateTokenSuccessfully() throws Exception {
    final var cpf = "12345678900";
    final var token =
        new ShareToken(
            1L, UUID.randomUUID().toString(), 15, LocalDateTime.of(2024, 1, 1, 10, 0), 1L);

    when(this.generateToken.execute(cpf, 15)).thenReturn(token);

    mockMvc
        .perform(
            post(String.format(BASE_URL_CPF_TOKEN, cpf))
                .param("expiresInMinutes", "15")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(token.getId()))
        .andExpect(jsonPath("$.accessToken").value(token.getAccessToken()))
        .andExpect(jsonPath("$.expiresInMinutes").value(token.getExpiresInMinutes()))
        .andExpect(jsonPath("$.createdAt", startsWith(token.getCreatedAt().toString())))
        .andExpect(jsonPath("$.patientId").value(token.getPatientId()));
  }

  @Test
  void shouldReturnHistorySuccessfully() throws Exception {
    final var cpf = "12345678900";
    final var medicalRecord =
        new MedicalRecord(
            1L,
            LocalDateTime.of(2024, 6, 1, 10, 0),
            "UBS Central",
            "Dra. Ana Lima",
            "Gripe",
            "Repouso e hidratação",
            "Revisar em 5 dias");

    when(this.searchPatientHistory.execute(cpf)).thenReturn(List.of(medicalRecord));

    mockMvc
        .perform(
            get(String.format(BASE_URL_CPF_HISTORY, cpf)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(medicalRecord.getId()))
        .andExpect(jsonPath("$[0].date", startsWith(medicalRecord.getDate().toString())))
        .andExpect(jsonPath("$[0].unit").value(medicalRecord.getUnit()))
        .andExpect(jsonPath("$[0].professionalName").value(medicalRecord.getProfessionalName()))
        .andExpect(jsonPath("$[0].diagnosis").value(medicalRecord.getDiagnosis()))
        .andExpect(jsonPath("$[0].treatment").value(medicalRecord.getTreatment()))
        .andExpect(jsonPath("$[0].notes").value(medicalRecord.getNotes()));
  }
}
