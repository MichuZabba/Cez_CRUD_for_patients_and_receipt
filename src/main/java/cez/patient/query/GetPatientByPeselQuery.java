package cez.patient.query;

import cez.common.cqrs.Query;
import cez.patient.dto.PatientResponse;

public record GetPatientByPeselQuery(String pesel) implements Query<PatientResponse> {}
