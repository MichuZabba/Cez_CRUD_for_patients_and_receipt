package cez.prescription.query;

import cez.common.cqrs.Query;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.model.Prescription;

import java.util.List;

public record GetAllPrescriptionByPeselQuery(
        String pesel
) implements Query<List<PrescriptionResponse>> {}
