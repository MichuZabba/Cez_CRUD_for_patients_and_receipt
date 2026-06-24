package cez.patient.service;

import cez.patient.dto.PatientResponse;
import cez.patient.query.SearchPatientsQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
                .filter(p -> (query.lastName() != null && !query.lastName().isBlank() &&
                        p.nazwisko().toLowerCase().contains(query.lastName().toLowerCase())) ||
                        (query.pesel() != null && !query.pesel().isBlank() &&
                                p.pesel().equals(query.pesel())) ||
                        ((query.lastName() == null || query.lastName().isBlank()) &&
                                (query.pesel() == null || query.pesel().isBlank())))
                .sorted(Comparator.comparing((Patient p) -> p.nazwisko().toLowerCase())
                        .thenComparing(p -> p.imie().toLowerCase()))
                .toList();

        int start = (int) query.pageable().getOffset();
        int end = Math.min(start + query.pageable().getPageSize(), filtered.size());

        if (start >= filtered.size()) {
            return new PageImpl<>(List.of(), query.pageable(), filtered.size());
        }

        return new PageImpl<>(
                filtered.subList(start, end).stream()
                        .map(p -> new PatientResponse(
                                p.pesel(),
                                p.nazwisko(),
                                p.imie()
                        ))
                        .toList(),
                query.pageable(),
                filtered.size()
        );
    }
}