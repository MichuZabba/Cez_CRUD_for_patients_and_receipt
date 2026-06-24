package cez.prescription.query;

import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.model.Prescription;
import cez.prescription.repository.IPrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPrescriptionByPeselQueryHandlerTest {

    @Mock
    private IPrescriptionRepository prescriptionRepository;

    @InjectMocks
    private GetAllPrescriptionByPeselQueryHandler handler;

    @Test
    void handle_shouldReturnMappedPrescriptionResponses() {
        // Arrange
        UUID id = UUID.randomUUID();
        List<Prescription> prescriptions = List.of(
                new Prescription(id, "12345678901", "Aspirin", 100.0)
        );
        when(prescriptionRepository.findAllByPesel("12345678901")).thenReturn(prescriptions);

        // Act
        List<PrescriptionResponse> result = handler.handle(new GetAllPrescriptionByPeselQuery("12345678901"));

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).prescriptionId()).isEqualTo(id);
        assertThat(result.get(0).pesel()).isEqualTo("12345678901");
        assertThat(result.get(0).nazwaLeku()).isEqualTo("Aspirin");
        assertThat(result.get(0).dawka()).isEqualTo(100.0);
    }

    @Test
    void handle_shouldReturnEmptyList_whenNoPrescriptionsFound() {
        // Arrange
        when(prescriptionRepository.findAllByPesel("99999999999")).thenReturn(List.of());

        // Act
        List<PrescriptionResponse> result = handler.handle(new GetAllPrescriptionByPeselQuery("99999999999"));

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void handle_shouldReturnAllPrescriptionsForGivenPesel() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0),
                new Prescription(UUID.randomUUID(), "12345678901", "Ibuprofen", 200.0)
        );
        when(prescriptionRepository.findAllByPesel("12345678901")).thenReturn(prescriptions);

        // Act
        List<PrescriptionResponse> result = handler.handle(new GetAllPrescriptionByPeselQuery("12345678901"));

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void handle_shouldCallRepositoryWithCorrectPesel() {
        // Arrange
        when(prescriptionRepository.findAllByPesel("12345678901")).thenReturn(List.of());

        // Act
        handler.handle(new GetAllPrescriptionByPeselQuery("12345678901"));

        // Assert
        verify(prescriptionRepository).findAllByPesel("12345678901");
    }
}
