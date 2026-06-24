package cez.patient.command;

import cez.common.cqrs.Command;

public record CreatePatientCommand(
        String pesel,
        String imie,
        String nazwisko
) implements Command {}
