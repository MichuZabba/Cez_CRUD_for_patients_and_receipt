package cez.prescription.service;

import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.query.SearchPrescriptionsQuery;
import org.springframework.data.domain.Page;

public interface IPrescriptionService {
    Page<PrescriptionResponse> searchPrescriptions(SearchPrescriptionsQuery query);
}
