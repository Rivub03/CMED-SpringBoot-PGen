package com.rivu.pgen.prescription_generator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class RxNavService {
    private final RestTemplate restTemplate;
    private static final String RXNAV_API_BASE_URL = "https://rxnav.nlm.nih.gov/REST";

    public RxNavService(RestTemplate restTemplate) {
        // NOTE: Spring Boot auto-configures RestTemplate, allowing it to be injected.
        this.restTemplate = restTemplate;
    }

    public  Map<String, Object> getApproximateMatch(String term) {
        if (term == null || term.trim().isEmpty()) {
            return Map.of("error", "Search term cannot be empty.");
        }

        // Construct the full URL with parameters safely encoded
        String url = UriComponentsBuilder.fromHttpUrl(RXNAV_API_BASE_URL)
                .path("/RxNormData/approximateMatch")
                .queryParam("term", term)
                .queryParam("maxEntries", 10) // Limit results
                .toUriString();

        try {
            // The API returns complex JSON, so we use Map<String, Object> to capture the whole structure.
            // A dedicated response DTO could be used for cleaner code, but Map is faster for a demo.
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Error calling RxNav API: " + e.getMessage());
            return Map.of("error", "Could not fetch data from RxNav. Check connection or API status.");
        }
    }
    /**
     * Fetches interaction data for a fixed RxCUI (341248 - Naproxen).
     * API: /REST/interaction/interaction.json?rxcui=341248
     * @return A Map representing the parsed JSON response.
     */
    public Map<String, Object> getNaproxenInteractions() {
        // The fixed RxCUI for Naproxen
        String rxcui = "341248";

        // --- FIX APPLIED HERE ---
        String url = UriComponentsBuilder.fromHttpUrl(RXNAV_API_BASE_URL)
                .path("/interaction") // Removed /interaction.json
                .queryParam("rxcui", rxcui)
                .queryParam("sources", "DrugBank") // Added a required source
                .toUriString();
        // ------------------------

        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            // Updated error message to include the source
            System.err.println("Error calling RxNav Interaction API: " + e.getMessage());
            return Map.of("error", "Could not fetch drug interaction data. API returned 404/error, possibly due to API deprecation or missing 'sources' parameter.");
        }
    }
}
