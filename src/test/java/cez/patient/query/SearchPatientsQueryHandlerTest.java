package cez.patient.query;

import cez.patient.dto.PatientResponse;
import cez.patient.service.IPatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPatientsQueryHandlerTest {

    @Mock
    private IPatientService patientService;

    @InjectMocks
    private SearchPatientsQueryHandler handler;

    @Test
    void handle_shouldDelegateToPatientService() {
        // Arrange
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "Kowalski", null);
        Page<PatientResponse> expectedPage = new PageImpl<>(List.of(
                new PatientResponse("12345678901", "Jan", "Kowalski")
        ));
        when(patientService.searchPatients(query)).thenReturn(expectedPage);

        // Act
        Page<PatientResponse> result = handler.handle(query);

        // Assert
        assertThat(result).isEqualTo(expectedPage);
        verify(patientService).searchPatients(query);
    }

    @Test
    void handle_shouldReturnEmptyPage_whenNoResults() {
        // Arrange
        SearchPatientsQuery query = new SearchPatientsQuery(PageRequest.of(0, 10), "NieIstniejacy", null);
        when(patientService.searchPatients(query)).thenReturn(Page.empty());

        // Act
        Page<PatientResponse> result = handler.handle(query);

        // Assert
        assertThat(result).isEmpty();
    }
}
