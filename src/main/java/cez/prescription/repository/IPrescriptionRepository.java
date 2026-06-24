package cez.prescription.repository;

import cez.prescription.model.Prescription;

import java.util.List;
import java.util.UUID;

public interface IPrescriptionRepository {
    void add(Prescription prescription);

    List<Prescription> findAllByPesel(String pesel);

    List<Prescription> findAll();

    void removeByPeselAndId(String pesel, UUID prescriptionId);
}
