package cez.prescription.controller;

import cez.prescription.command.CreatePrescriptionCommand;
import cez.prescription.command.CreatePrescriptionCommandHandler;
import cez.prescription.command.DeletePrescriptionCommand;
import cez.prescription.command.DeletePrescriptionCommandHandler;
import cez.prescription.dto.CreatePrescriptionRequest;
import cez.prescription.dto.DeletePrescriptionRequest;
import cez.prescription.dto.PrescriptionPagedRequest;
import cez.prescription.dto.PrescriptionResponse;
import cez.prescription.query.GetAllPrescriptionByPeselQuery;
import cez.prescription.query.GetAllPrescriptionByPeselQueryHandler;
import cez.prescription.query.SearchPrescriptionsQuery;
import cez.prescription.query.SearchPrescriptionsQueryHandler;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@EnableSpringDataWebSupport
public class PrescriptionController {

    private final CreatePrescriptionCommandHandler createPrescriptionCommandHandler;
    private final GetAllPrescriptionByPeselQueryHandler getAllPrescriptionByPeselQueryHandler;
    private final DeletePrescriptionCommandHandler deletePrescriptionCommandHandler;
    private final SearchPrescriptionsQueryHandler searchPrescriptionsQueryHandler;

    public PrescriptionController(CreatePrescriptionCommandHandler createPrescriptionCommandHandler, GetAllPrescriptionByPeselQueryHandler getAllPrescriptionByPeselQueryHandler, DeletePrescriptionCommandHandler deletePrescriptionCommandHandler, SearchPrescriptionsQueryHandler searchPrescriptionsQueryHandler) {
        this.createPrescriptionCommandHandler = createPrescriptionCommandHandler;
        this.getAllPrescriptionByPeselQueryHandler = getAllPrescriptionByPeselQueryHandler;
        this.deletePrescriptionCommandHandler = deletePrescriptionCommandHandler;
        this.searchPrescriptionsQueryHandler = searchPrescriptionsQueryHandler;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        CreatePrescriptionCommand command = new CreatePrescriptionCommand(
                request.pesel(),
                request.nazwaLeku(),
                request.dawka()
        );
        createPrescriptionCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/search")
    public ResponseEntity<Page<PrescriptionResponse>> searchPrescriptions(
            @RequestBody PrescriptionPagedRequest request
    ) {
        Pageable pageable = PageRequest.of(request.page(), request.size());

        SearchPrescriptionsQuery query = new SearchPrescriptionsQuery(pageable, request.nazwaLeku(), request.pesel());
        Page<PrescriptionResponse> result = searchPrescriptionsQueryHandler.handle(query);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{pesel}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionByPesel(@PathVariable String pesel) {
        List<PrescriptionResponse> response = getAllPrescriptionByPeselQueryHandler.handle(new GetAllPrescriptionByPeselQuery(pesel));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePrescription(@RequestBody DeletePrescriptionRequest request) {
        deletePrescriptionCommandHandler.handle(new DeletePrescriptionCommand(request.prescriptionId(), request.pesel()));
        return ResponseEntity.noContent().build();
    }
}
