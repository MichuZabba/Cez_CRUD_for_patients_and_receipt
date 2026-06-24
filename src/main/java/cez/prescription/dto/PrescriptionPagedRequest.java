package cez.prescription.dto;

public record PrescriptionPagedRequest(
        String nazwaLeku,
        String pesel,
        int page,
        int size
) {}
