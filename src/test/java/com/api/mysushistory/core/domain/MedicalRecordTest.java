package com.api.mysushistory.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.mysushistory.core.domain.exception.DomainException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MedicalRecordTest {

  @Test
  void shouldCreateMedicalRecordSuccessfully() {
    final var dateTime = LocalDateTime.now().withNano(0);
    final var medicalRecord =
        MedicalRecord.createMedicalRecord(
            dateTime,
            "UBS Central",
            "Dr. José Silva",
            "Gripe",
            "Descanso e hidratação",
            "Retornar em uma semana");

    assertThat(medicalRecord.getDate()).isEqualTo(dateTime);
    assertThat(medicalRecord.getUnit()).isEqualTo("UBS Central");
    assertThat(medicalRecord.getProfessionalName()).isEqualTo("Dr. José Silva");
    assertThat(medicalRecord.getDiagnosis()).isEqualTo("Gripe");
    assertThat(medicalRecord.getTreatment()).isEqualTo("Descanso e hidratação");
    assertThat(medicalRecord.getNotes()).isEqualTo("Retornar em uma semana");
  }

  @Test
  void shouldNotCreateWhenDateIsNull() {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    null, "UBS Central", "Dr. José Silva", "Gripe", "Descanso", "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[date] should not be empty or null by domain medical record");
  }

  @Test
  void shouldNotCreateWhenDateIsInFuture() {
    var future = LocalDateTime.now().plusDays(1);
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    future, "UBS Central", "Dr. José Silva", "Gripe", "Descanso", "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[date] must not be in the future by domain medical record");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreateWhenUnitIsBlank(final String invalidUnit) {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    LocalDateTime.now(),
                    invalidUnit,
                    "Dr. José Silva",
                    "Gripe",
                    "Descanso",
                    "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[unit] should not be empty or null by domain medical record");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreateWhenProfessionalNameIsBlank(final String invalidProfessionalName) {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    LocalDateTime.now(),
                    "UBS Central",
                    invalidProfessionalName,
                    "Gripe",
                    "Descanso",
                    "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage(
            "Field=[professionalName] should not be empty or null by domain medical record");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreateWhenDiagnosisIsBlank(final String invalidDiagnosis) {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    LocalDateTime.now(),
                    "UBS Central",
                    "Dr. José Silva",
                    invalidDiagnosis,
                    "Descanso",
                    "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[diagnosis] should not be empty or null by domain medical record");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreateWhenTreatmentIsBlank(final String invalidTreatment) {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    LocalDateTime.now(),
                    "UBS Central",
                    "Dr. José Silva",
                    "Gripe",
                    invalidTreatment,
                    "Notas"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[treatment] should not be empty or null by domain medical record");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreateWhenNotesIsBlank(final String invalidNotes) {
    assertThatThrownBy(
            () ->
                MedicalRecord.createMedicalRecord(
                    LocalDateTime.now(),
                    "UBS Central",
                    "Dr. José Silva",
                    "Gripe",
                    "Descanso",
                    invalidNotes))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[notes] should not be empty or null by domain medical record");
  }
}
