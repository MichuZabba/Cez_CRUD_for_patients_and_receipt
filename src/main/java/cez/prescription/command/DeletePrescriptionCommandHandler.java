package cez.prescription.command;

import cez.common.cqrs.CommandHandler;
import cez.prescription.repository.IPrescriptionRepository;
import cez.prescription.repository.PrescriptionRepository;
import org.springframework.stereotype.Component;

@Component
public class DeletePrescriptionCommandHandler implements CommandHandler<DeletePrescriptionCommand> {

    private final IPrescriptionRepository prescriptionRepository;

    public DeletePrescriptionCommandHandler(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public void handle(DeletePrescriptionCommand command) {
        prescriptionRepository.removeByPeselAndId(command.pesel(),command.prescriptionId());
    }
}
