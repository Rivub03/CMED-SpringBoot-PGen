package com.rivu.pgen.prescription_generator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    // Keep this to handle the root URL, redirecting to the primary list page
    @GetMapping("/")
    public String home() {
        return "redirect:/prescriptions";
    }
    // Keep the login mapping
    @GetMapping("/login")
    public String login() {
        return "login"; // Maps to /templates/login.html
    }
}

