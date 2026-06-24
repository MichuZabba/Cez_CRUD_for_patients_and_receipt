package cez.patient.controller;

import cez.common.exception.GlobalExceptionHandler;
import cez.patient.command.CreatePatientCommandHandler;
import cez.patient.command.DeletePatientCommandHandler;
import cez.patient.dto.PatientResponse;
import cez.patient.query.GetPatientByPeselQueryHandler;
import cez.patient.query.SearchPatientsQueryHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private CreatePatientCommandHandler createCommandHandler;

    @Mock
    private GetPatientByPeselQueryHandler getPatientByPeselQueryHandler;

    @Mock
    private SearchPatientsQueryHandler searchPatientsQueryHandler;

    @Mock
    private DeletePatientCommandHandler deleteCommandHandler;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PatientController controller = new PatientController(
                createCommandHandler,
                getPatientByPeselQueryHandler,
                searchPatientsQueryHandler,
                deleteCommandHandler
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- GET /patients/{pesel} ---

    @Test
    void getPatientByPesel_shouldReturn200WithPatientResponse() throws Exception {
        // Arrange
        PatientResponse response = new PatientResponse("12345678901", "Jan", "Kowalski");
        when(getPatientByPeselQueryHandler.handle(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/patients/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pesel").value("12345678901"))
                .andExpect(jsonPath("$.imie").value("Jan"))
                .andExpect(jsonPath("$.nazwisko").value("Kowalski"));
    }

    @Test
    void getPatientByPesel_shouldReturn200_whenPatientNotFound() throws Exception {
        // Arrange
        when(getPatientByPeselQueryHandler.handle(any())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/patients/99999999999"))
                .andExpect(status().isOk());
    }

    // --- POST /patients/create ---

    @Test
    void createPatient_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "12345678901",
                "imie", "Jan",
                "nazwisko", "Kowalski"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(createCommandHandler).handle(any());
    }

    @Test
    void createPatient_shouldReturn400_whenPeselIsTooShort() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "123",
                "imie", "Jan",
                "nazwisko", "Kowalski"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createCommandHandler);
    }

    @Test
    void createPatient_shouldReturn400_whenPeselContainsLetters() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "1234567890A",
                "imie", "Jan",
                "nazwisko", "Kowalski"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_shouldReturn400_whenImieIsBlank() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "12345678901",
                "imie", "",
                "nazwisko", "Kowalski"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_shouldReturn400_whenNazwiskoIsTooShort() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "12345678901",
                "imie", "Jan",
                "nazwisko", "K"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_shouldReturn400_whenNazwiskoExceeds50Characters() throws Exception {
        // Arrange
        Map<String, String> request = Map.of(
                "pesel", "12345678901",
                "imie", "Jan",
                "nazwisko", "K".repeat(51)
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_shouldReturn400_whenRepositoryThrowsIllegalArgument() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Patient with this PESEL already exists"))
                .when(createCommandHandler).handle(any());

        Map<String, String> request = Map.of(
                "pesel", "12345678901",
                "imie", "Jan",
                "nazwisko", "Kowalski"
        );

        // Act & Assert
        mockMvc.perform(post("/patients/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Patient with this PESEL already exists"));
    }

    // --- DELETE /patients/{pesel} ---

    @Test
    void deletePatient_shouldReturn204() throws Exception {
        // Arrange
        // brak dodatkowych danych - testujemy sam endpoint

        // Act & Assert
        mockMvc.perform(delete("/patients/12345678901"))
                .andExpect(status().isNoContent());

        verify(deleteCommandHandler).handle(any());
    }

    // --- POST /patients/search ---

    @Test
    void searchPatients_shouldReturn200WithPagedResults() throws Exception {
        // Arrange
        List<PatientResponse> patients = List.of(
                new PatientResponse("12345678901", "Jan", "Kowalski")
        );
        when(searchPatientsQueryHandler.handle(any()))
                .thenReturn(new PageImpl<>(patients, PageRequest.of(0, 10), 1));

        Map<String, Object> request = Map.of(
                "page", 0,
                "size", 10,
                "nazwisko", "Kowalski",
                "pesel", ""
        );

        // Act & Assert
        mockMvc.perform(post("/patients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].pesel").value("12345678901"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchPatients_shouldReturn200WithEmptyPage_whenNoResults() throws Exception {
        // Arrange
        when(searchPatientsQueryHandler.handle(any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        Map<String, Object> request = Map.of(
                "page", 0,
                "size", 10,
                "nazwisko", "",
                "pesel", ""
        );

        // Act & Assert
        mockMvc.perform(post("/patients/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
