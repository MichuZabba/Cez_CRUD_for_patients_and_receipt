package cez.patient.command;

import cez.patient.repository.IPatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePatientCommandHandlerTest {

    @Mock
    private IPatientRepository patientRepository;

    @InjectMocks
    private DeletePatientCommandHandler handler;

    @Test
    void handle_shouldCallRemoveByPesel() {
        // Arrange
        DeletePatientCommand command = new DeletePatientCommand("12345678901");

        // Act
        handler.handle(command);

        // Assert
        verify(patientRepository).removeByPesel("12345678901");
    }

    @Test
    void handle_shouldCallRemoveByPeselWithCorrectValue() {
        // Arrange
        String pesel = "98765432109";

        // Act
        handler.handle(new DeletePatientCommand(pesel));

        // Assert
        verify(patientRepository).removeByPesel(pesel);
        verifyNoMoreInteractions(patientRepository);
    }
}
