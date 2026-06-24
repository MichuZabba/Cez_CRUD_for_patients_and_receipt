package cez.patient.query;

import cez.common.cqrs.Query;
import cez.patient.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public record SearchPatientsQuery(
        Pageable pageable,
        String lastName,
        String pesel
) implements Query<Page<PatientResponse>> {}
