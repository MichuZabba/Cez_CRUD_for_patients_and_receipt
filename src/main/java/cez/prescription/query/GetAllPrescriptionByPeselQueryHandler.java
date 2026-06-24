package cez.prescription.query;

import cez.common.cqrs.QueryHandler;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.repository.IPrescriptionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllPrescriptionByPeselQueryHandler implements QueryHandler<GetAllPrescriptionByPeselQuery, List<PrescriptionResponse>> {
    private final IPrescriptionRepository prescriptionRepository;

    public GetAllPrescriptionByPeselQueryHandler(IPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public List<PrescriptionResponse> handle(GetAllPrescriptionByPeselQuery query) {
        return prescriptionRepository.findAllByPesel(query.pesel()).stream()
                .map(prescription -> new PrescriptionResponse(
                        prescription.prescriptionId(),
                        prescription.pesel(),
                        prescription.nazwaLeku(),
                        prescription.dawka()
                ))
                .toList();
    }
}
