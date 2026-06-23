package cez.prescription.repository;

import cez.prescription.model.Prescription;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PrescriptionRepository implements IPrescriptionRepository {
    private ConcurrentHashMap<String, List<Prescription>> prescriptions = new ConcurrentHashMap<>();

    public void add(Prescription prescription) {
        if(prescriptions.putIfAbsent(prescription.pesel(), new ArrayList<>(List.of(prescription))) != null) {
            prescriptions.get(prescription.pesel()).add(prescription);
        }
    }
    public List<Prescription> findAllByPesel(String pesel) {
        return prescriptions.getOrDefault(pesel, List.of());
    }

    public void removeByPesel(String pesel) {
        prescriptions.remove(pesel);
    }

    public List<Prescription> findAll() {
        return new ArrayList<>(prescriptions.values()).stream().flatMap(List::stream).toList();
    }
}
