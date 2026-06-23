package cez.prescription.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.model.Prescription;
import cez.prescription.query.SearchPrescriptionsQuery;
import cez.prescription.repository.IPrescriptionRepository;

import java.util.List;

@Service
public class PrescriptionService {

    private final IPrescriptionRepository prescriptionRepository;

    public PrescriptionService(IPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public Page<PrescriptionResponse> searchPrescriptions(SearchPrescriptionsQuery query) {
        List<Prescription> allPrescriptions = prescriptionRepository.findAll();

        List<Prescription> filtered = allPrescriptions.stream()
                .filter(p -> (query.nazwaLeku() != null && !query.nazwaLeku().isBlank() &&
                        p.nazwaLeku().toLowerCase().contains(query.nazwaLeku().toLowerCase())) ||
                        (query.pesel() != null && !query.pesel().isBlank() &&
                                p.pesel().equals(query.pesel())) ||
                        ((query.nazwaLeku() == null || query.nazwaLeku().isBlank()) &&
                                (query.pesel() == null || query.pesel().isBlank())))
                .toList();

        int total = filtered.size();
        int start = (int) query.pageable().getOffset();
        int end = Math.min((start + query.pageable().getPageSize()), total);

        if (start > total) {
            return new PageImpl<>(List.of(), query.pageable(), total);
        }

        List<PrescriptionResponse> pageContent = filtered.subList(start, end).stream()
                .map(p -> new PrescriptionResponse(p.pesel(), p.nazwaLeku(), p.dawka()))
                .toList();

        return new PageImpl<>(pageContent, query.pageable(), total);
    }
}