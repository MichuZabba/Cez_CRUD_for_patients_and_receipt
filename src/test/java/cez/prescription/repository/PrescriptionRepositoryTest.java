package cez.prescription.repository;

import cez.prescription.model.Prescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class PrescriptionRepositoryTest {

    private PrescriptionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PrescriptionRepository();
    }

    @Test
    void add_shouldAddPrescriptionForNewPesel() {
        // Arrange
        Prescription prescription = new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0);

        // Act
        repository.add(prescription);

        // Assert
        assertThat(repository.findAllByPesel("12345678901")).hasSize(1);
    }

    @Test
    void add_shouldAddMultiplePrescriptionsForSamePesel() {
        // Arrange
        Prescription p1 = new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0);
        Prescription p2 = new Prescription(UUID.randomUUID(), "12345678901", "Ibuprofen", 200.0);

        // Act
        repository.add(p1);
        repository.add(p2);

        // Assert
        assertThat(repository.findAllByPesel("12345678901")).hasSize(2);
    }

    @Test
    void add_shouldAddPrescriptionsForDifferentPesels() {
        // Arrange
        Prescription p1 = new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0);
        Prescription p2 = new Prescription(UUID.randomUUID(), "98765432109", "Ibuprofen", 200.0);

        // Act
        repository.add(p1);
        repository.add(p2);

        // Assert
        assertThat(repository.findAllByPesel("12345678901")).hasSize(1);
        assertThat(repository.findAllByPesel("98765432109")).hasSize(1);
    }

    @Test
    void findAllByPesel_shouldReturnEmptyList_whenNotFound() {
        // Arrange
        // puste repozytorium

        // Act
        List<Prescription> result = repository.findAllByPesel("99999999999");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByPesel_shouldReturnCorrectPrescriptions() {
        // Arrange
        UUID id = UUID.randomUUID();
        Prescription prescription = new Prescription(id, "12345678901", "Aspirin", 100.0);
        repository.add(prescription);

        // Act
        List<Prescription> result = repository.findAllByPesel("12345678901");

        // Assert
        assertThat(result).containsExactly(prescription);
    }

    @Test
    void removeByPeselAndId_shouldRemovePrescription() {
        // Arrange
        UUID id = UUID.randomUUID();
        repository.add(new Prescription(id, "12345678901", "Aspirin", 100.0));

        // Act
        repository.removeByPeselAndId("12345678901", id);

        // Assert
        assertThat(repository.findAllByPesel("12345678901")).isEmpty();
    }

    @Test
    void removeByPeselAndId_shouldRemoveOnlyMatchingPrescription() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Prescription p2 = new Prescription(id2, "12345678901", "Ibuprofen", 200.0);
        repository.add(new Prescription(id1, "12345678901", "Aspirin", 100.0));
        repository.add(p2);

        // Act
        repository.removeByPeselAndId("12345678901", id1);

        // Assert
        assertThat(repository.findAllByPesel("12345678901")).containsExactly(p2);
    }

    @Test
    void removeByPeselAndId_shouldRemoveMapEntryWhenLastPrescriptionDeleted() {
        // Arrange
        UUID id = UUID.randomUUID();
        repository.add(new Prescription(id, "12345678901", "Aspirin", 100.0));

        // Act
        repository.removeByPeselAndId("12345678901", id);

        // Assert
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void removeByPeselAndId_shouldDoNothing_whenPeselNotFound() {
        // Arrange
        // puste repozytorium

        // Act & Assert
        assertThatCode(() -> repository.removeByPeselAndId("99999999999", UUID.randomUUID()))
                .doesNotThrowAnyException();
    }

    @Test
    void findAll_shouldReturnAllPrescriptionsFromAllPesels() {
        // Arrange
        repository.add(new Prescription(UUID.randomUUID(), "12345678901", "Aspirin", 100.0));
        repository.add(new Prescription(UUID.randomUUID(), "12345678901", "Ibuprofen", 200.0));
        repository.add(new Prescription(UUID.randomUUID(), "98765432109", "Paracetamol", 500.0));

        // Act
        List<Prescription> result = repository.findAll();

        // Assert
        assertThat(result).hasSize(3);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoPrescriptions() {
        // Arrange
        // puste repozytorium

        // Act & Assert
        assertThat(repository.findAll()).isEmpty();
    }
}
