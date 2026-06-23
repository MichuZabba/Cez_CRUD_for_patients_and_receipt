package cez.patient.service;

import cez.patient.query.SearchPatientsQuery;
import cez.patient.dto.PatientPagedResponse;
import org.springframework.data.domain.Page;

public interface IPatientService {

     Page<PatientPagedResponse> searchPatients(SearchPatientsQuery query);
}
