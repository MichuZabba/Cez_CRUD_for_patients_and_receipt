package cez.patient.query;

import cez.common.cqrs.QueryHandler;
import cez.patient.dto.PatientPagedRequest;
import cez.patient.dto.PatientResponse;
import cez.patient.service.IPatientService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SearchPatientsQueryHandler implements QueryHandler<SearchPatientsQuery, Page<PatientResponse>> {

    private final IPatientService patientService;

    public SearchPatientsQueryHandler(IPatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public Page<PatientResponse> handle(SearchPatientsQuery query) {
        return patientService.searchPatients(query);
    }
}
