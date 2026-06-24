package cez.patient.dto;

public record PatientPagedRequest(
    String pesel,
    String nazwisko,
    String imie,
    int page,
    int size
){}
