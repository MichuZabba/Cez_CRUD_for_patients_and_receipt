package cez.patient.query;

import cez.common.cqrs.QueryHandler;
import cez.patient.dto.PatientResponse;
import cez.patient.model.Patient;
import cez.patient.repository.IPatientRepository;
import org.springframework.stereotype.Component;

@Component
public class GetPatientByPeselQueryHandler implements QueryHandler<GetPatientByPeselQuery, PatientResponse> {

    private final IPatientRepository patientRepository;

    public GetPatientByPeselQueryHandler(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PatientResponse handle(GetPatientByPeselQuery query) {
        Patient patient = patientRepository.findByPesel(query.pesel());

        if (patient == null) {
            return null;
        }

        return new PatientResponse(patient.pesel(), patient.imie(), patient.nazwisko());
    }
}
