package cez.patient.repository;

import cez.patient.model.Patient;

import java.util.ArrayList;
import java.util.List;

public interface IPatientRepository {
     void add(Patient patient);

     void removeByPesel(String pesel);

     Patient findByPesel(String pesel);

     List<Patient> findAll();
}
