package cez.prescription.controller;

import cez.common.exception.GlobalExceptionHandler;
import cez.prescription.command.CreatePrescriptionCommandHandler;
import cez.prescription.command.DeletePrescriptionCommandHandler;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.query.GetAllPrescriptionByPeselQueryHandler;
import cez.prescription.query.SearchPrescriptionsQueryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionControllerTest {

    @Mock
    private CreatePrescriptionCommandHandler createPrescriptionCommandHandler;

    @Mock
    private GetAllPrescriptionByPeselQueryHandler getAllPrescriptionByPeselQueryHandler;

    @Mock
    private DeletePrescriptionCommandHandler deletePrescriptionCommandHandler;

    @Mock
    private SearchPrescriptionsQueryHandler searchPrescriptionsQueryHandler;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PrescriptionController controller = new PrescriptionController(
                createPrescriptionCommandHandler,
                getAllPrescriptionByPeselQueryHandler,
                deletePrescriptionCommandHandler,
                searchPrescriptionsQueryHandler
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- POST /prescriptions/create ---

    @Test
    void createPrescription_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "12345678901",
                "nazwaLeku", "Aspirin",
                "dawka", 100.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(createPrescriptionCommandHandler).handle(any());
    }

    @Test
    void createPrescription_shouldReturn400_whenPeselIsTooShort() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "123",
                "nazwaLeku", "Aspirin",
                "dawka", 100.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createPrescriptionCommandHandler);
    }

    @Test
    void createPrescription_shouldReturn400_whenPeselContainsLetters() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "1234567890A",
                "nazwaLeku", "Aspirin",
                "dawka", 100.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrescription_shouldReturn400_whenNazwaLekuIsBlank() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "12345678901",
                "nazwaLeku", "",
                "dawka", 100.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrescription_shouldReturn400_whenDawkaIsNegative() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "12345678901",
                "nazwaLeku", "Aspirin",
                "dawka", -10.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrescription_shouldReturn400_whenDawkaIsZero() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "12345678901",
                "nazwaLeku", "Aspirin",
                "dawka", 0.0
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPrescription_shouldReturn400_whenDawkaIsMissing() throws Exception {
        // Arrange
        Map<String, Object> request = Map.of(
                "pesel", "12345678901",
                "nazwaLeku", "Aspirin"
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- GET /prescriptions/{pesel} ---

    @Test
    void getPrescriptionByPesel_shouldReturn200WithPrescriptionList() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        List<PrescriptionResponse> responses = List.of(
                new PrescriptionResponse(id, "12345678901", "Aspirin", 100.0)
        );
        when(getAllPrescriptionByPeselQueryHandler.handle(any())).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/prescriptions/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pesel").value("12345678901"))
                .andExpect(jsonPath("$[0].nazwaLeku").value("Aspirin"))
                .andExpect(jsonPath("$[0].dawka").value(100.0))
                .andExpect(jsonPath("$[0].prescriptionId").value(id.toString()));
    }

    @Test
    void getPrescriptionByPesel_shouldReturn200WithEmptyList_whenNoPrescriptions() throws Exception {
        // Arrange
        when(getAllPrescriptionByPeselQueryHandler.handle(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/prescriptions/99999999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // --- DELETE /prescriptions/delete ---

    @Test
    void deletePrescription_shouldReturn204() throws Exception {
        // Arrange
        UUID prescriptionId = UUID.randomUUID();
        Map<String, String> request = Map.of(
                "prescriptionId", prescriptionId.toString(),
                "pesel", "12345678901"
        );

        // Act & Assert
        mockMvc.perform(delete("/prescriptions/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(deletePrescriptionCommandHandler).handle(any());
    }

    // --- POST /prescriptions/search ---

    @Test
    void searchPrescriptions_shouldReturn200WithPagedResults() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        List<PrescriptionResponse> prescriptions = List.of(
                new PrescriptionResponse(id, "12345678901", "Aspirin", 100.0)
        );
        when(searchPrescriptionsQueryHandler.handle(any()))
                .thenReturn(new PageImpl<>(prescriptions, PageRequest.of(0, 10), 1));

        Map<String, Object> request = Map.of(
                "page", 0,
                "size", 10,
                "nazwaLeku", "Aspirin",
                "pesel", ""
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nazwaLeku").value("Aspirin"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchPrescriptions_shouldReturn200WithEmptyPage_whenNoResults() throws Exception {
        // Arrange
        when(searchPrescriptionsQueryHandler.handle(any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        Map<String, Object> request = Map.of(
                "page", 0,
                "size", 10,
                "nazwaLeku", "",
                "pesel", ""
        );

        // Act & Assert
        mockMvc.perform(post("/prescriptions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
