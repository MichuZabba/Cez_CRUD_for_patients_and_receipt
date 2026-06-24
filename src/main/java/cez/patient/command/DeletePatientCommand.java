package cez.patient.command;

import cez.common.cqrs.Command;

public record DeletePatientCommand(String pesel) implements Command {}
