package cez.prescription.service;

import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.model.Prescription;
import cez.prescription.query.SearchPrescriptionsQuery;
import cez.prescription.repository.IPrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private IPrescriptionRepository prescriptionRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    @Test
    void searchPrescriptions_shouldReturnAll_whenNoFiltersProvided() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0),
                new Prescription(UUID.randomUUID(), "98765432109", "Ibuprofen", 200.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), null, null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchPrescriptions_shouldFilterByNazwaLeku() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0),
                new Prescription(UUID.randomUUID(), "98765432109", "Ibuprofen", 200.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "Aspirin", null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).nazwaLeku()).isEqualTo("Aspirin");
    }

    @Test
    void searchPrescriptions_shouldFilterByNazwaLekuCaseInsensitive() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0),
                new Prescription(UUID.randomUUID(), "98765432109", "Ibuprofen", 200.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "aspirin", null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchPrescriptions_shouldFilterByNazwaLekuPartialMatch() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin 100mg", 100.0),
                new Prescription(UUID.randomUUID(), "98765432109", "Aspirin 200mg", 200.0),
                new Prescription(UUID.randomUUID(), "11111111111", "Ibuprofen", 400.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "Aspirin", null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchPrescriptions_shouldFilterByPesel() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0),
                new Prescription(UUID.randomUUID(), "98765432109", "Ibuprofen", 200.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), null, "12345678901");

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).pesel()).isEqualTo("12345678901");
    }

    @Test
    void searchPrescriptions_shouldReturnCorrectPage() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "11111111111", "Lek A", 10.0),
                new Prescription(UUID.randomUUID(), "22222222222", "Lek B", 20.0),
                new Prescription(UUID.randomUUID(), "33333333333", "Lek C", 30.0),
                new Prescription(UUID.randomUUID(), "44444444444", "Lek D", 40.0),
                new Prescription(UUID.randomUUID(), "55555555555", "Lek E", 50.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(1, 2), null, null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    @Test
    void searchPrescriptions_shouldReturnEmptyPage_whenOffsetBeyondResults() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(5, 10), null, null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchPrescriptions_shouldReturnAll_whenBlankFilters() {
        // Arrange
        List<Prescription> prescriptions = List.of(
                new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "  ", "  ");

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchPrescriptions_shouldMapAllFieldsCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        List<Prescription> prescriptions = List.of(
                new Prescription(id, "12345678901", "Aspirin", 100.0)
        );
        when(prescriptionRepository.findAll()).thenReturn(prescriptions);
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), null, null);

        // Act
        Page<PrescriptionResponse> result = prescriptionService.searchPrescriptions(query);

        // Assert
        PrescriptionResponse response = result.getContent().get(0);
        assertThat(response.prescriptionId()).isEqualTo(id);
        assertThat(response.pesel()).isEqualTo("12345678901");
        assertThat(response.nazwaLeku()).isEqualTo("Aspirin");
        assertThat(response.dawka()).isEqualTo(100.0);
    }
}
