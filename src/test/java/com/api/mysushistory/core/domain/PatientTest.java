package com.api.mysushistory.core.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.mysushistory.core.domain.exception.DomainException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PatientTest {

  @Test
  void shouldCreatePatientSuccessfully() {
    final var patient = Patient.createPatient("John Doe", "12345678900", LocalDate.of(1990, 1, 1));

    assertThat(patient.getName()).isEqualTo("John Doe");
    assertThat(patient.getCpf()).isEqualTo("12345678900");
    assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    assertThat(patient.getMedicalRecords()).isNotNull().isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreatePatientWhenNameIsBlank(final String invalidName) {
    assertThatThrownBy(
            () -> Patient.createPatient(invalidName, "12345678900", LocalDate.of(1990, 1, 1)))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[name] should not be empty or null by domain client");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void shouldNotCreatePatientWhenCpfIsBlank(final String invalidCpf) {
    assertThatThrownBy(
            () -> Patient.createPatient("John Doe", invalidCpf, LocalDate.of(1990, 1, 1)))
        .isInstanceOf(DomainException.class)
        .hasMessage(
            "Field=[cpf] should not be empty or null by domain client, The field=[cpf] has an invalid pattern by domain client");
  }

  @ParameterizedTest
  @ValueSource(strings = {"123", "abcdefghijk", "1234567890a"})
  void shouldNotCreatePatientWhenCpfPatternInvalid(final String invalidCpf) {
    assertThatThrownBy(
            () -> Patient.createPatient("John Doe", invalidCpf, LocalDate.of(1990, 1, 1)))
        .isInstanceOf(DomainException.class)
        .hasMessage("The field=[cpf] has an invalid pattern by domain client");
  }

  @Test
  void shouldNotCreatePatientWhenBirthDateIsNull() {
    assertThatThrownBy(() -> Patient.createPatient("John Doe", "12345678900", null))
        .isInstanceOf(DomainException.class)
        .hasMessage("Field=[birthDate] should not be empty or null by domain client");
  }
}
