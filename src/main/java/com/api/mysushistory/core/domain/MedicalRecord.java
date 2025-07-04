package com.api.mysushistory.core.domain;

import static java.lang.String.format;

import com.api.mysushistory.core.domain.exception.DomainException;
import com.api.mysushistory.core.domain.valueobject.ValidationDomain;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedicalRecord {

  private static final String DOMAIN_MESSAGE_ERROR = "by domain medical record";
  private static final String BLANK_MESSAGE_ERROR = "Field=[%s] should not be empty or null";
  private static final String FUTURE_DATE_ERROR = "Field=[%s] must not be in the future";

  private Long id;
  private LocalDateTime date;
  private String unit;
  private String professionalName;
  private String diagnosis;
  private String treatment;
  private String notes;

  public MedicalRecord() {}

  public MedicalRecord(
      final Long id,
      final LocalDateTime date,
      final String unit,
      final String professionalName,
      final String diagnosis,
      final String treatment,
      final String notes) {

    validateDomain(date, unit, professionalName, diagnosis, treatment, notes);

    this.id = id;
    this.date = date;
    this.unit = unit;
    this.professionalName = professionalName;
    this.diagnosis = diagnosis;
    this.treatment = treatment;
    this.notes = notes;
  }

  public static MedicalRecord createMedicalRecord(
      final LocalDateTime date,
      final String unit,
      final String professionalName,
      final String diagnosis,
      final String treatment,
      final String notes) {

    validateDomain(date, unit, professionalName, diagnosis, treatment, notes);

    return new MedicalRecord(null, date, unit, professionalName, diagnosis, treatment, notes);
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getUnit() {
    return unit;
  }

  public String getProfessionalName() {
    return professionalName;
  }

  public String getDiagnosis() {
    return diagnosis;
  }

  public String getTreatment() {
    return treatment;
  }

  public String getNotes() {
    return notes;
  }

  private static void validateDomain(
      final LocalDateTime date,
      final String unit,
      final String professionalName,
      final String diagnosis,
      final String treatment,
      final String notes) {

    final List<ValidationDomain<?>> rules =
        List.of(
            new ValidationDomain<>(
                date, format(BLANK_MESSAGE_ERROR, "date"), List.of(Objects::isNull)),
            new ValidationDomain<>(
                date,
                format(FUTURE_DATE_ERROR, "date"),
                List.of(d -> d != null && d.isAfter(LocalDateTime.now()))),
            new ValidationDomain<>(
                unit,
                format(BLANK_MESSAGE_ERROR, "unit"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                professionalName,
                format(BLANK_MESSAGE_ERROR, "professionalName"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                diagnosis,
                format(BLANK_MESSAGE_ERROR, "diagnosis"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                treatment,
                format(BLANK_MESSAGE_ERROR, "treatment"),
                List.of(Objects::isNull, String::isBlank)),
            new ValidationDomain<>(
                notes,
                format(BLANK_MESSAGE_ERROR, "notes"),
                List.of(Objects::isNull, String::isBlank)));

    final var errors = validate(rules);

    if (!errors.isEmpty()) {
      throw new DomainException(errors);
    }
  }

  private static List<String> validate(final List<ValidationDomain<?>> validations) {
    return validations.stream()
        .filter(MedicalRecord::isInvalid)
        .map(it -> format("%s %s", it.message(), DOMAIN_MESSAGE_ERROR))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private static <T> boolean isInvalid(final ValidationDomain<T> domain) {
    return domain.predicates().stream().anyMatch(p -> p.test(domain.field()));
  }
}
