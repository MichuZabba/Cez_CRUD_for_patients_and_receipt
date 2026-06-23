package cez.patient.repository;

import org.springframework.stereotype.Repository;
import cez.patient.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PatientRepository implements IPatientRepository {
    private ConcurrentHashMap<String, Patient> patients = new ConcurrentHashMap<>();

    public void add(Patient patient) {
        if (patients.putIfAbsent(patient.pesel(), patient) != null) {
            throw new IllegalArgumentException("Patient with this PESEL already exists");
        }
    }
    public void removeByPesel(String pesel) {
        patients.remove(pesel);
    }

    public Patient findByPesel(String pesel) {
        return patients.getOrDefault(pesel, null);
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }
}
