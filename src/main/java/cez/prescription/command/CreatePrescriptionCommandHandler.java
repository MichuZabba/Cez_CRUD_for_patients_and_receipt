package cez.prescription.command;

import cez.common.cqrs.CommandHandler;
import cez.prescription.model.Prescription;
import cez.prescription.repository.IPrescriptionRepository;
import cez.prescription.repository.PrescriptionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreatePrescriptionCommandHandler implements CommandHandler<CreatePrescriptionCommand> {
    private final IPrescriptionRepository prescriptionRepository;

    public CreatePrescriptionCommandHandler(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public void handle(CreatePrescriptionCommand command) {
        Prescription prescription = new Prescription(UUID.randomUUID(),command.pesel(), command.nazwaLeku(), command.dawka());
        prescriptionRepository.add(prescription);
    }
}
