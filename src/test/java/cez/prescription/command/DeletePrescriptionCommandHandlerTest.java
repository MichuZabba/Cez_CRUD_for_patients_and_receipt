package cez.prescription.command;

import cez.prescription.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePrescriptionCommandHandlerTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private DeletePrescriptionCommandHandler handler;

    @Test
    void handle_shouldCallRemoveByPeselAndId() {
        // Arrange
        UUID prescriptionId = UUID.randomUUID();
        DeletePrescriptionCommand command = new DeletePrescriptionCommand(prescriptionId, "12345678901");

        // Act
        handler.handle(command);

        // Assert
        verify(prescriptionRepository).removeByPeselAndId("12345678901", prescriptionId);
    }

    @Test
    void handle_shouldPassExactValuesToRepository() {
        // Arrange
        UUID prescriptionId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String pesel = "98765432109";

        // Act
        handler.handle(new DeletePrescriptionCommand(prescriptionId, pesel));

        // Assert
        verify(prescriptionRepository).removeByPeselAndId(pesel, prescriptionId);
        verifyNoMoreInteractions(prescriptionRepository);
    }
}
