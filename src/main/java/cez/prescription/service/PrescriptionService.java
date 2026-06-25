package cez.prescription.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.model.Prescription;
import cez.prescription.query.SearchPrescriptionsQuery;
import cez.prescription.repository.IPrescriptionRepository;

import java.util.List;

@Service
public class PrescriptionService implements IPrescriptionService {

    private final IPrescriptionRepository prescriptionRepository;

    public PrescriptionService(IPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public Page<PrescriptionResponse> searchPrescriptions(SearchPrescriptionsQuery query) {
        List<Prescription> filtered = prescriptionRepository.findAll().stream()
                .filter(prescription -> matchesCriteria(prescription, query))
                .toList();

        return createPage(filtered, query.pageable());
    }

    private boolean matchesCriteria(Prescription prescription, SearchPrescriptionsQuery query) {
        boolean hasMedicationQuery = isNotEmpty(query.nazwaLeku());
        boolean hasPeselQuery = isNotEmpty(query.pesel());

        if (!hasMedicationQuery && !hasPeselQuery) {
            return true;
        }

        boolean matchesMedication = hasMedicationQuery &&
                prescription.nazwaLeku().toLowerCase().contains(query.nazwaLeku().toLowerCase());

        boolean matchesPesel = hasPeselQuery &&
                prescription.pesel().equals(query.pesel());

        return matchesMedication || matchesPesel;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private Page<PrescriptionResponse> createPage(List<Prescription> prescriptions, Pageable pageable) {
        int total = prescriptions.size();
        int start = (int) pageable.getOffset();

        if (start > total) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        int end = Math.min(start + pageable.getPageSize(), total);

        List<PrescriptionResponse> pageContent = prescriptions.subList(start, end).stream()
                .map(this::toPrescriptionResponse)
                .toList();

        return new PageImpl<>(pageContent, pageable, total);
    }

    private PrescriptionResponse toPrescriptionResponse(Prescription prescription) {
        return new PrescriptionResponse(
                prescription.prescriptionId(),
                prescription.pesel(),
                prescription.nazwaLeku(),
                prescription.dawka()
        );
    }
}