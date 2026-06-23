package cez.prescription.command;

import cez.common.cqrs.Command;

public record DeletePrescriptionCommand(
        String pesel
) implements Command {}
