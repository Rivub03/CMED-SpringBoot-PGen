package com.rivu.pgen.prescription_generator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @NonNull()
    @Column(nullable = false)
    private LocalDate prescriptionDate; // Prescription Date (valid date, mandatory)

    @Column(nullable = false)
    private String patientName; // Patient Name (text, mandatory)

    @NotNull()
    @Column(nullable = false)
    private Integer patientAge; // Patient Age (integer, valid age range, mandatory)

    @Column(nullable = false)
    private String patientGender; // Patient Gender (select box, mandatory)

    @Lob // Use @Lob for potentially large text areas (like diagnosis and medicines)
    private String diagnosis; // Diagnosis (text area)

    @Lob
    @Column(nullable = false) // Assuming medicines list is crucial
    private String medicines; // Medicines (Text area, mandatory based on typical workflow)

    private LocalDate nextVisitDate; // Next visit date (valid date, optional)

    // Optional: Add a field to track which user created the prescription
    // @ManyToOne
    // private User createdBy;
}
