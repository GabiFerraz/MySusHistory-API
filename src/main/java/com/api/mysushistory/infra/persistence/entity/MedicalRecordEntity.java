package com.api.mysushistory.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Column(name = "unit", nullable = false)
  private String unit;

  @Column(name = "professional_name", nullable = false)
  private String professionalName;

  @Column(name = "diagnosis", nullable = false)
  private String diagnosis;

  @Column(name = "treatment", nullable = false)
  private String treatment;

  @Column(name = "notes")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id", nullable = false)
  private PatientEntity patient;
}
