package cez.prescription.dto;

import jakarta.validation.constraints.*;

public record CreatePrescriptionRequest(
        @NotBlank(message = "PESEL nie może być pusty")
        @Size(min = 11, max = 11, message = "PESEL must be exactly 11 characters long")
        @Pattern(regexp = "\\d+", message = "PESEL must contain only digits")
        String pesel,

        @NotBlank(message = "Nazwa leku nie może być pusta")
        String nazwaLeku,

        @NotNull(message = "Dawka nie może być pusta")
        @Positive(message = "Dawka musi być większa od zera")
        Double dawka
) {}