package com.rivu.pgen.prescription_generator.service;

import com.rivu.pgen.prescription_generator.entity.Prescription;
import com.rivu.pgen.prescription_generator.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // --- CRUD Operations ---

    /** Saves a new or updated prescription. */
    public Prescription save(Prescription prescription) {
        // add business validation logic (e.g., check age range)
        if (prescription.getPatientAge() < 0 || prescription.getPatientAge() > 120) {
            throw new IllegalArgumentException("Invalid patient age.");
        }
        return prescriptionRepository.save(prescription);
    }

    /** Finds a prescription by ID. */
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prescription Id:" + id));
    }

    /** Deletes a prescription by ID. */
    public void deleteById(Long id) {
        prescriptionRepository.deleteById(id);
    }


    // --- Retrieval and Filtering ---
    /*** Fetches prescriptions within a specified date range.
     * This fulfills the primary listing requirement.
     */
    public List<Prescription> findByDateRange(LocalDate startDate, LocalDate endDate) {
        // Ensure dates are not null before querying
        if (startDate == null || endDate == null) {
            // Default to current month if dates are missing
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }
        return prescriptionRepository.findByPrescriptionDateBetween(startDate, endDate);
    }

    // --- Reporting ---
    /** * Generates the day-wise prescription count report.
     * The result is a List of Object arrays: [Day (LocalDate), Count (Long)]
     */
    public List<Object[]> getDayWiseCount(LocalDate startDate, LocalDate endDate) {
        // Use the same default date range logic as the list view
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }
        return prescriptionRepository.getDayWiseCount(startDate, endDate);
    }
}
