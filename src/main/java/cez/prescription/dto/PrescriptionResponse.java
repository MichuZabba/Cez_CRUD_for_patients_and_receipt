package cez.prescription.dto;

public record PrescriptionResponse(
        String pesel,
        String nazwaLeku,
        Double dawka
) {}
