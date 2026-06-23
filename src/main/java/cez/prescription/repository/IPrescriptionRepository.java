package cez.prescription.repository;

import cez.prescription.model.Prescription;

import java.util.List;

public interface IPrescriptionRepository {
    void add(Prescription prescription);

    List<Prescription> findAllByPesel(String pesel);

    void removeByPesel(String pesel);

    List<Prescription> findAll();
}
