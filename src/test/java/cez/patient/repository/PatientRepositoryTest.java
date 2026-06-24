package cez.patient.repository;

import cez.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PatientRepositoryTest {

    private PatientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PatientRepository();
    }

    @Test
    void add_shouldAddPatient() {
        // Arrange
        Patient patient = new Patient("12345678901", "Jan", "Kowalski");

        // Act
        repository.add(patient);

        // Assert
        assertThat(repository.findByPesel("12345678901")).isEqualTo(patient);
    }

    @Test
    void add_shouldThrowWhenDuplicatePesel() {
        // Arrange
        Patient patient = new Patient("12345678901", "Jan", "Kowalski");
        repository.add(patient);

        // Act & Assert
        assertThatThrownBy(() -> repository.add(patient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Patient with this PESEL already exists");
    }

    @Test
    void add_shouldThrowWhenDifferentPatientWithSamePesel() {
        // Arrange
        repository.add(new Patient("12345678901", "Jan", "Kowalski"));

        // Act & Assert
        assertThatThrownBy(() -> repository.add(new Patient("12345678901", "Anna", "Nowak")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findByPesel_shouldReturnPatient() {
        // Arrange
        Patient patient = new Patient("12345678901", "Jan", "Kowalski");
        repository.add(patient);

        // Act
        Patient result = repository.findByPesel("12345678901");

        // Assert
        assertThat(result).isEqualTo(patient);
    }

    @Test
    void findByPesel_shouldReturnNull_whenNotFound() {
        // Arrange
        // puste repozytorium

        // Act
        Patient result = repository.findByPesel("99999999999");

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void removeByPesel_shouldRemovePatient() {
        // Arrange
        repository.add(new Patient("12345678901", "Jan", "Kowalski"));

        // Act
        repository.removeByPesel("12345678901");

        // Assert
        assertThat(repository.findByPesel("12345678901")).isNull();
    }

    @Test
    void removeByPesel_shouldNotFailWhenPeselNotFound() {
        // Arrange
        // puste repozytorium

        // Act & Assert
        assertThatCode(() -> repository.removeByPesel("99999999999"))
                .doesNotThrowAnyException();
    }

    @Test
    void findAll_shouldReturnAllPatients() {
        // Arrange
        repository.add(new Patient("12345678901", "Jan", "Kowalski"));
        repository.add(new Patient("98765432109", "Anna", "Nowak"));

        // Act
        List<Patient> result = repository.findAll();

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoPatients() {
        // Arrange
        // puste repozytorium

        // Act & Assert
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void findAll_shouldReturnCopy_notDirectReference() {
        // Arrange
        repository.add(new Patient("12345678901", "Jan", "Kowalski"));

        // Act
        List<Patient> result1 = repository.findAll();
        List<Patient> result2 = repository.findAll();

        // Assert
        assertThat(result1).isNotSameAs(result2);
    }
}
