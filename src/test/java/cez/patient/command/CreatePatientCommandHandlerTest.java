package cez.patient.command;

import cez.patient.model.Patient;
import cez.patient.repository.IPatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePatientCommandHandlerTest {

    @Mock
    private IPatientRepository patientRepository;

    @InjectMocks
    private CreatePatientCommandHandler handler;

    @Test
    void handle_shouldCreateAndSavePatient() {
        // Arrange
        CreatePatientCommand command = new CreatePatientCommand("12345678901", "Jan", "Kowalski");

        // Act
        handler.handle(command);

        // Assert
        verify(patientRepository).add(new Patient("12345678901", "Jan", "Kowalski"));
    }

    @Test
    void handle_shouldPropagateExceptionFromRepository() {
        // Arrange
        CreatePatientCommand command = new CreatePatientCommand("12345678901", "Jan", "Kowalski");
        doThrow(new IllegalArgumentException("Patient with this PESEL already exists"))
                .when(patientRepository).add(any());

        // Act & Assert
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Patient with this PESEL already exists");
    }
}
