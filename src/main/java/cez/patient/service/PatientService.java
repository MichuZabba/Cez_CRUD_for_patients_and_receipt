package cez.patient.service;

import cez.patient.dto.PatientResponse;
import cez.patient.query.SearchPatientsQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import cez.patient.model.Patient;
import cez.patient.repository.PatientRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class PatientService implements IPatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Page<PatientResponse> searchPatients(SearchPatientsQuery query) {
        List<Patient> filtered = patientRepository.findAll().stream()
                .filter(patient -> matchesCriteria(patient, query))
                .sorted(getPatientComparator())
                .toList();

        return createPage(filtered, query.pageable());
    }


    private boolean matchesCriteria(Patient patient, SearchPatientsQuery query) {
        boolean hasLastNameQuery = isNotEmpty(query.lastName());
        boolean hasPeselQuery = isNotEmpty(query.pesel());

        if (!hasLastNameQuery && !hasPeselQuery) {
            return true;
        }

        boolean matchesLastName = hasLastNameQuery &&
                patient.nazwisko().toLowerCase().contains(query.lastName().toLowerCase());

        boolean matchesPesel = hasPeselQuery &&
                patient.pesel().equals(query.pesel());

        return matchesLastName || matchesPesel;
    }

    private Comparator<Patient> getPatientComparator() {
        return Comparator.comparing((Patient p) -> p.nazwisko().toLowerCase())
                .thenComparing(p -> p.imie().toLowerCase());
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }


    private Page<PatientResponse> createPage(List<Patient> patients, Pageable pageable) {
        int start = (int) pageable.getOffset();
        if (start >= patients.size()) {
            return new PageImpl<>(List.of(), pageable, patients.size());
        }

        int end = Math.min(start + pageable.getPageSize(), patients.size());

        List<PatientResponse> content = patients.subList(start, end).stream()
                .map(this::toPatientResponse)
                .toList();

        return new PageImpl<>(content, pageable, patients.size());
    }

    private PatientResponse toPatientResponse(Patient patient) {
        return new PatientResponse(
                patient.pesel(),
                patient.nazwisko(),
                patient.imie()
        );
    }
}