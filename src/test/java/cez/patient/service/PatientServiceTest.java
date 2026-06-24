package cez.patient.service;

import cez.patient.dto.PatientResponse;
import cez.patient.model.Patient;
import cez.patient.query.SearchPatientsQuery;
import cez.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void searchPatients_shouldReturnAllPatients_whenNoFiltersProvided() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski"),
                new Patient("98765432109", "Anna", "Nowak")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), null, null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchPatients_shouldFilterByLastName() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski"),
                new Patient("98765432109", "Anna", "Nowak")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "Kowalski", null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).pesel()).isEqualTo("12345678901");
    }

    @Test
    void searchPatients_shouldFilterByLastNameCaseInsensitive() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski"),
                new Patient("98765432109", "Anna", "Nowak")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "kowalski", null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchPatients_shouldFilterByLastNamePartialMatch() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski"),
                new Patient("98765432109", "Janina", "Kowalczyk"),
                new Patient("11111111111", "Anna", "Nowak")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "Kowal", null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchPatients_shouldFilterByPesel() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski"),
                new Patient("98765432109", "Anna", "Nowak")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), null, "12345678901");

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).pesel()).isEqualTo("12345678901");
    }

    @Test
    void searchPatients_shouldReturnSortedByLastNameThenFirstName() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("11111111111", "Zbigniew", "Nowak"),
                new Patient("22222222222", "Anna", "Kowalski"),
                new Patient("33333333333", "Adam", "Kowalski")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), null, null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        List<PatientResponse> content = result.getContent();
        assertThat(content.get(0).pesel()).isEqualTo("33333333333"); // Adam Kowalski
        assertThat(content.get(1).pesel()).isEqualTo("22222222222"); // Anna Kowalski
        assertThat(content.get(2).pesel()).isEqualTo("11111111111"); // Zbigniew Nowak
    }

    @Test
    void searchPatients_shouldReturnCorrectPage() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("11111111111", "A", "A"),
                new Patient("22222222222", "B", "B"),
                new Patient("33333333333", "C", "C"),
                new Patient("44444444444", "D", "D"),
                new Patient("55555555555", "E", "E")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(1, 2), null, null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    @Test
    void searchPatients_shouldReturnEmptyPage_whenOffsetBeyondResults() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(5, 10), null, null);

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchPatients_shouldReturnAll_whenBlankFilters() {
        // Arrange
        List<Patient> patients = List.of(
                new Patient("12345678901", "Jan", "Kowalski")
        );
        when(patientRepository.findAll()).thenReturn(patients);
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "  ", "  ");

        // Act
        Page<PatientResponse> result = patientService.searchPatients(query);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
