package cez.prescription.command;

import cez.common.cqrs.Command;

import java.util.UUID;

public record DeletePrescriptionCommand(
        UUID prescriptionId,
        String pesel
) implements Command {}
