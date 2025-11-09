package com.rivu.pgen.prescription_generator.repository;

import com.rivu.pgen.prescription_generator.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Requirement: Show a list of prescriptions generated for a particular date range.
    List<Prescription> findByPrescriptionDateBetween(LocalDate startDate, LocalDate endDate);

    // Requirement: Create a report: Day-wise prescription count [day, prescription count]
    // Corrected Query: Using H2-compatible TRUNC and setting nativeQuery to true
    @Query(value = "SELECT TRUNC(p.prescription_date), COUNT(p.id) FROM prescription p " +
            "WHERE p.prescription_date BETWEEN :startDate AND :endDate " +
            "GROUP BY TRUNC(p.prescription_date) ORDER BY 1",
            nativeQuery = true) // <-- THIS IS THE KEY FIX
    List<Object[]> getDayWiseCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
