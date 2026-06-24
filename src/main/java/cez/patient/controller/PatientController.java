package cez.patient.controller;

import cez.patient.query.GetPatientByPeselQuery;
import cez.patient.query.GetPatientByPeselQueryHandler;
import cez.patient.query.SearchPatientsQuery;
import cez.patient.query.SearchPatientsQueryHandler;
import cez.patient.command.CreatePatientCommand;
import cez.patient.command.CreatePatientCommandHandler;
import cez.patient.command.DeletePatientCommand;
import cez.patient.command.DeletePatientCommandHandler;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cez.patient.dto.PatientCreateRequest;
import cez.patient.dto.PatientPagedRequest;
import cez.patient.dto.PatientResponse;

@RestController
@RequestMapping("/patients")
@EnableSpringDataWebSupport
public class PatientController {

    private final CreatePatientCommandHandler createCommandHandler;
    private final GetPatientByPeselQueryHandler getPatientByPeselQueryHandler;
    private final SearchPatientsQueryHandler searchPatientsQueryHandler;
    private final DeletePatientCommandHandler deleteCommandHandler;

    public PatientController(CreatePatientCommandHandler createCommandHandler,
                             GetPatientByPeselQueryHandler getPatientByPeselQueryHandler,
                             SearchPatientsQueryHandler searchPatientsQueryHandler,
                             DeletePatientCommandHandler deleteCommandHandler) {
        this.createCommandHandler = createCommandHandler;
        this.getPatientByPeselQueryHandler = getPatientByPeselQueryHandler;
        this.searchPatientsQueryHandler = searchPatientsQueryHandler;
        this.deleteCommandHandler = deleteCommandHandler;
    }

    @GetMapping("/{pesel}")
    public ResponseEntity<PatientResponse> getPatientByPesel(@PathVariable String pesel) {
        PatientResponse response = getPatientByPeselQueryHandler.handle(new GetPatientByPeselQuery(pesel));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<PatientResponse>> getPatients(
            @RequestBody PatientPagedRequest request) {

        Pageable pageable = PageRequest.of(request.page(), request.size());
        SearchPatientsQuery query = new SearchPatientsQuery(pageable, request.nazwisko(), request.pesel());
        Page<PatientResponse> result = searchPatientsQueryHandler.handle(query);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        CreatePatientCommand command = new CreatePatientCommand(request.pesel(), request.imie(), request.nazwisko());
        createCommandHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{pesel}")
    public ResponseEntity<Void> removePatient(@PathVariable String pesel) {
        deleteCommandHandler.handle(new DeletePatientCommand(pesel));

        return ResponseEntity.noContent().build();
    }
}
