package cez.prescription.dto;

import java.util.UUID;

public record PrescriptionResponse(
        UUID prescriptionId,
        String pesel,
        String nazwaLeku,
        Double dawka
) {}
