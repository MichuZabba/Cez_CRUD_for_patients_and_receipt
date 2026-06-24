package cez.prescription.query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import cez.common.cqrs.QueryHandler;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.service.PrescriptionService;

@Component
public class SearchPrescriptionsQueryHandler implements QueryHandler<SearchPrescriptionsQuery, Page<PrescriptionResponse>> {

    private final PrescriptionService prescriptionService;

    public SearchPrescriptionsQueryHandler(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Override
    public Page<PrescriptionResponse> handle(SearchPrescriptionsQuery query) {
        return prescriptionService.searchPrescriptions(query);
    }
}