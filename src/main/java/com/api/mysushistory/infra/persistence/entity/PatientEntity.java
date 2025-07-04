package com.api.mysushistory.infra.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cpf", unique = true, nullable = false, length = 11)
  private String cpf;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Builder.Default
  @OneToMany(
      mappedBy = "patient",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<MedicalRecordEntity> medicalRecords = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ShareTokenEntity> shareTokens = new ArrayList<>();
}
