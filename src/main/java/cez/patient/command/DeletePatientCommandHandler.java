package cez.patient.command;

import cez.common.cqrs.CommandHandler;
import cez.patient.repository.IPatientRepository;
import cez.patient.repository.PatientRepository;
import org.springframework.stereotype.Component;

@Component
public class DeletePatientCommandHandler implements CommandHandler<DeletePatientCommand> {

    private final IPatientRepository patientRepository;

    public DeletePatientCommandHandler(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void handle(DeletePatientCommand command) {
        patientRepository.removeByPesel(command.pesel());
    }
}
