package cez.patient.service;

import cez.patient.dto.PatientResponse;
import cez.patient.query.SearchPatientsQuery;
import org.springframework.data.domain.Page;

public interface IPatientService {

     Page<PatientResponse> searchPatients(SearchPatientsQuery query);
}
