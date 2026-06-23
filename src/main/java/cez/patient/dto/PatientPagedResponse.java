package cez.patient.dto;

public record PatientPagedResponse(
    String pesel,
    String nazwisko,
    String imie,
    int page,
    int size
){}
