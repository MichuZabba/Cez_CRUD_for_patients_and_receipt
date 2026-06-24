package cez.prescription.dto;

import java.util.UUID;

public record DeletePrescriptionRequest(UUID prescriptionId, String pesel) {}
