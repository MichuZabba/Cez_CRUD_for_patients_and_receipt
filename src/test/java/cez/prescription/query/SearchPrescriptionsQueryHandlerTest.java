package cez.prescription.query;

import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.service.IPrescriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPrescriptionsQueryHandlerTest {

    @Mock
    private IPrescriptionService prescriptionService;

    @InjectMocks
    private SearchPrescriptionsQueryHandler handler;

    @Test
    void handle_shouldDelegateToPrescriptionService() {
        // Arrange
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "Aspirin", null);
        Page<PrescriptionResponse> expectedPage = new PageImpl<>(List.of(
                new PrescriptionResponse(UUID.randomUUID(), "12345678901", "Aspirin", 100.0)
        ));
        when(prescriptionService.searchPrescriptions(query)).thenReturn(expectedPage);

        // Act
        Page<PrescriptionResponse> result = handler.handle(query);

        // Assert
        assertThat(result).isEqualTo(expectedPage);
        verify(prescriptionService).searchPrescriptions(query);
    }

    @Test
    void handle_shouldReturnEmptyPage_whenNoResults() {
        // Arrange
        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(PageRequest.of(0, 10), "NieIstniejacyLek", null);
        when(prescriptionService.searchPrescriptions(query)).thenReturn(Page.empty());

        // Act
        Page<PrescriptionResponse> result = handler.handle(query);

        // Assert
        assertThat(result).isEmpty();
    }
}
