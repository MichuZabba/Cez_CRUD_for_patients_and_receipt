package cez.patient.query;

import cez.common.cqrs.QueryHandler;
import cez.patient.dto.PatientPagedResponse;
import cez.patient.service.IPatientService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SearchPatientsQueryHandler implements QueryHandler<SearchPatientsQuery, Page<PatientPagedResponse>> {

    private final IPatientService patientService;

    public SearchPatientsQueryHandler(IPatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public Page<PatientPagedResponse> handle(SearchPatientsQuery query) {
        return patientService.searchPatients(query);
    }
}
