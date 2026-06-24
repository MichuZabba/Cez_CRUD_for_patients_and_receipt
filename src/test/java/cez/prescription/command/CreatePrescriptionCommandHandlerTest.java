package cez.prescription.command;

import cez.prescription.model.Prescription;
import cez.prescription.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePrescriptionCommandHandlerTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @InjectMocks
    private CreatePrescriptionCommandHandler handler;

    @Test
    void handle_shouldSavePrescriptionWithCorrectData() {
        // Arrange
        CreatePrescriptionCommand command = new CreatePrescriptionCommand("12345678901", "Aspirin", 100.0);
        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);

        // Act
        handler.handle(command);

        // Assert
        verify(prescriptionRepository).add(captor.capture());
        Prescription saved = captor.getValue();
        assertThat(saved.pesel()).isEqualTo("12345678901");
        assertThat(saved.nazwaLeku()).isEqualTo("Aspirin");
        assertThat(saved.dawka()).isEqualTo(100.0);
    }

    @Test
    void handle_shouldGenerateUniqueUuidForEachPrescription() {
        // Arrange
        CreatePrescriptionCommand command = new CreatePrescriptionCommand("12345678901", "Aspirin", 100.0);
        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);

        // Act
        handler.handle(command);
        handler.handle(command);

        // Assert
        verify(prescriptionRepository, times(2)).add(captor.capture());
        assertThat(captor.getAllValues().get(0).prescriptionId())
                .isNotEqualTo(captor.getAllValues().get(1).prescriptionId());
    }
}
