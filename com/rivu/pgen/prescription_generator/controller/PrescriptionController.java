package com.rivu.pgen.prescription_generator.controller;

import com.rivu.pgen.prescription_generator.entity.Prescription;
import com.rivu.pgen.prescription_generator.service.PrescriptionService;
import com.rivu.pgen.prescription_generator.service.RxNavService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.rivu.pgen.prescription_generator.service.RxNavService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    private final RxNavService rxNavService;

    public PrescriptionController(PrescriptionService prescriptionService, RxNavService rxNavService) {
        this.prescriptionService = prescriptionService;
        this.rxNavService = rxNavService;
    }



    // Requirement: Show a list of prescriptions generated for a particular date range (default current month)
    @GetMapping({"", "/"})
    public String listPrescriptions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        // Use the Service to handle the default date logic
        List<Prescription> prescriptions = prescriptionService.findByDateRange(startDate, endDate);

        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "prescriptions/list"; // Maps to src/main/resources/templates/prescriptions/list.html
    }
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("prescription", new Prescription());
        model.addAttribute("genders", List.of("Male", "Female", "Other")); // For select box
        return "prescriptions/form";
    }

    @PostMapping("/new")
    public String createPrescription(@Valid @ModelAttribute("prescription") Prescription prescription,
                                     BindingResult result,
                                     RedirectAttributes attributes,
                                     Model model) {
        // Requirement: show proper error messages when invalid data is submitted
        if (result.hasErrors()) {
            // Re-add necessary model attributes for the form template
            model.addAttribute("genders", List.of("Male", "Female", "Other"));
            return "prescriptions/form";
        }

        try {
            prescriptionService.save(prescription);
            attributes.addFlashAttribute("message", "Prescription created successfully!");
        } catch (IllegalArgumentException e) {
            // Handle business logic errors (like invalid age range)
            result.rejectValue("patientAge", "error.prescription", e.getMessage());
            model.addAttribute("genders", List.of("Male", "Female", "Other"));
            return "prescriptions/form";
        }

        return "redirect:/prescriptions";
    }
    @GetMapping("/prescriptions/new")
    public String newPrescription(Model model) {
        return "prescriptions/form";
    }

    // Requirement: Allow user to edit a prescription entry

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionService.findById(id);
        model.addAttribute("prescription", prescription);
        model.addAttribute("genders", List.of("Male", "Female", "Other"));
        return "prescriptions/form";
    }

    // Note: The POST for edit uses the same logic as POST for new, but the ID field ensures it's an update.

    // Requirement: Allow the user to delete a prescription entity
    @GetMapping("/delete/{id}")
    public String deletePrescription(@PathVariable Long id, RedirectAttributes attributes) {
        prescriptionService.deleteById(id);
        attributes.addFlashAttribute("message", "Prescription deleted successfully.");
        return "redirect:/prescriptions";
    }

    // Requirement: Create a report: Day-wise prescription count
    @GetMapping("/report/day-wise-count")
    public String showReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        // Use the Service to fetch the aggregate report data
        List<Object[]> reportData = prescriptionService.getDayWiseCount(startDate, endDate);

        model.addAttribute("reportData", reportData);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "report/count"; // Maps to src/main/resources/templates/report/count.html
    }

    @GetMapping("/rxnav-data")
    public String showRxNavInteractions(Model model) { // Removed @RequestParam String term

        Map<String, Object> apiResponse = rxNavService.getNaproxenInteractions();

        // the full response to the model
        model.addAttribute("apiResponse", apiResponse);

        List<Map<String, Object>> interactionList = List.of();

        try {
            // The structure is highly nested:
            // { interactionTypeGroup: [ { interactionType: [ { interactionPair: [ {...} ] } ] } ] }
            List<Map<String, Object>> groups = (List<Map<String, Object>>) apiResponse.get("interactionTypeGroup");

            if (groups != null && !groups.isEmpty()) {
                List<Map<String, Object>> types = (List<Map<String, Object>>) groups.get(0).get("interactionType");

                if (types != null && !types.isEmpty()) {
                    // We'll focus on the interactionPair list within the first type
                    List<Map<String, Object>> pairs = (List<Map<String, Object>>) types.get(0).get("interactionPair");

                    if (pairs != null) {
                        interactionList = pairs;
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("dataError", "Failed to parse interaction data: " + e.getMessage());
        }

        model.addAttribute("interactionList", interactionList);
        model.addAttribute("targetDrug", "Naproxen (RxCUI 341248)"); // Context for the view

        return "external/rxnav-interactions"; // <-- Use a new, specific view template
    }
}
