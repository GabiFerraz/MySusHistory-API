package com.api.mysushistory.core.domain;

import static java.lang.String.format;

import com.api.mysushistory.core.domain.exception.DomainException;
import com.api.mysushistory.core.domain.valueobject.ValidationDomain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Patient {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain client";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String PATTERN_ERROR_MESSAGE = "The field=[%s] has an invalid pattern";
  private static final Predicate<String> PATTERN_CPF =
      n -> n == null || !Pattern.compile("\\d{11}").matcher(n).matches();

  private Long id;
  private String name;
  private String cpf;
  private LocalDate birthDate;
  private List<MedicalRecord> medicalRecords = new ArrayList<>();

  public Patient() {}

  public Patient(
      final Long id,
      final String name,
      final String cpf,
      final LocalDate birthDate,
      final List<MedicalRecord> medicalRecords) {

    validateDomain(name, cpf, birthDate);

    this.id = id;
    this.name = name;
    this.cpf = cpf;
    this.birthDate = birthDate;
    this.medicalRecords = medicalRecords == null ? new ArrayList<>() : List.copyOf(medicalRecords);
  }

  public static Patient createPatient(
      final String name, final String cpf, final LocalDate birthDate) {

    validateDomain(name, cpf, birthDate);

    return new Patient(null, name, cpf, birthDate, List.of());
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCpf() {
    return cpf;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public List<MedicalRecord> getMedicalRecords() {
    return medicalRecords;
  }

  private static void validateDomain(
      final String name, final String cpf, final LocalDate birthDate) {
    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                name,
                format(BLANK_MESSAGE_ERROR, "name"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                cpf, format(BLANK_MESSAGE_ERROR, "cpf"), List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(cpf, format(PATTERN_ERROR_MESSAGE, "cpf"), List.of(PATTERN_CPF)),
            new ValidationDomain<>(
                birthDate, format(BLANK_MESSAGE_ERROR, "birthDate"), List.of(Objects::isNull)));

    final var errors = validate(rules);

    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(Patient::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(predicate -> predicate.test(domain.field()));
  }
}
