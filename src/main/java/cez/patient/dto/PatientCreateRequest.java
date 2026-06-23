package cez.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientCreateRequest(
        @NotBlank(message = "PESEL cannot be empty")
        @Size(min = 11, max = 11, message = "PESEL must be exactly 11 characters long")
        @Pattern(regexp = "\\d+", message = "PESEL must contain only digits")
        String pesel,

        @NotBlank(message = "First name cannot be empty")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String imie,

        @NotBlank(message = "Last name cannot be empty")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String nazwisko
) {}
