package cez.prescription.model;

import java.util.UUID;

public record Prescription(UUID prescriptionId, String pesel, String nazwaLeku, Double dawka){}
