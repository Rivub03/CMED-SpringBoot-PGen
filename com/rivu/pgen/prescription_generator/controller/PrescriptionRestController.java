package com.rivu.pgen.prescription_generator.controller;

import com.rivu.pgen.prescription_generator.entity.Prescription;
import com.rivu.pgen.prescription_generator.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;
import java.util.List;

@RestController // Marks this as a REST controller that returns data, not views
@RequestMapping("/api/v1/prescription")
public class PrescriptionRestController {
    private final PrescriptionService prescriptionService;

    public PrescriptionRestController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    /**
     * Requirement: Create a REST API to get the prescription list.
     * Accessible at GET /api/v1/prescription
     */
    @GetMapping
    public ResponseEntity<List<Prescription>> getPrescriptionList(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        // Reuses the service method to fetch data, including default month logic
        List<Prescription> prescriptions = prescriptionService.findByDateRange(startDate, endDate);

        // Returns the list with an HTTP 200 OK status.
        // Spring automatically converts the List<Prescription> to JSON.
        return ResponseEntity.ok(prescriptions);
    }
    // Requirement: Consume the external API and show data
    @GetMapping("/rxnav-data")
    public String showRxNavData() {
        return "external/rxnav-data"; // Maps to src/main/resources/templates/external/rxnav-data.html
    }
}
