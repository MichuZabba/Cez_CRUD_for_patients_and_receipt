package cez.patient.command;

import cez.common.cqrs.CommandHandler;
import cez.patient.model.Patient;
import cez.patient.repository.IPatientRepository;
import org.springframework.stereotype.Component;

@Component
public class CreatePatientCommandHandler implements CommandHandler<CreatePatientCommand> {

    private final IPatientRepository patientRepository;

    public CreatePatientCommandHandler(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void handle(CreatePatientCommand command) {
        Patient patient = new Patient(command.pesel(), command.imie(), command.nazwisko());
        patientRepository.add(patient);
    }
}
