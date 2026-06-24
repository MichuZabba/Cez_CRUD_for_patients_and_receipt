package cez.prescription.command;

import cez.common.cqrs.Command;

public record CreatePrescriptionCommand(
        String pesel,
        String nazwaLeku,
        double dawka
) implements Command {}
