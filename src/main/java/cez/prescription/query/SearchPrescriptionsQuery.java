package cez.prescription.query;

import cez.common.cqrs.Query;
import cez.prescription.dto.PrescriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public record SearchPrescriptionsQuery(
        Pageable pageable,
        String nazwaLeku,
        String pesel
) implements Query<Page<PrescriptionResponse>> {}
