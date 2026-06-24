package cez.patient.query;

import cez.patient.dto.PatientResponse;
import cez.patient.model.Patient;
import cez.patient.repository.IPatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPatientByPeselQueryHandlerTest {

    @Mock
    private IPatientRepository patientRepository;

    @InjectMocks
    private GetPatientByPeselQueryHandler handler;

    @Test
    void handle_shouldReturnPatientResponse_whenPatientExists() {
        // Arrange
        Patient patient = new Patient("12345678901", "Jan", "Kowalski");
        when(patientRepository.findByPesel("12345678901")).thenReturn(patient);

        // Act
        PatientResponse result = handler.handle(new GetPatientByPeselQuery("12345678901"));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pesel()).isEqualTo("12345678901");
        assertThat(result.imie()).isEqualTo("Jan");
        assertThat(result.nazwisko()).isEqualTo("Kowalski");
    }

    @Test
    void handle_shouldReturnNull_whenPatientNotFound() {
        // Arrange
        when(patientRepository.findByPesel("99999999999")).thenReturn(null);

        // Act
        PatientResponse result = handler.handle(new GetPatientByPeselQuery("99999999999"));

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void handle_shouldCallRepositoryWithCorrectPesel() {
        // Arrange
        when(patientRepository.findByPesel("12345678901")).thenReturn(
                new Patient("12345678901", "Jan", "Kowalski")
        );

        // Act
        handler.handle(new GetPatientByPeselQuery("12345678901"));

        // Assert
        verify(patientRepository).findByPesel("12345678901");
    }
}
