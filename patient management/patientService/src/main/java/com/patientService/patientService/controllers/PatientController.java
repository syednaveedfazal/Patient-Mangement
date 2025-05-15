package com.patientService.patientService.controllers;

import com.patientService.patientService.dtos.PatientRequestDTO;
import com.patientService.patientService.dtos.PatientResponseDTO;
import com.patientService.patientService.entities.Patient;
import com.patientService.patientService.kafka.KafkaProducer;
import com.patientService.patientService.kafka.RecommendationStream;
import com.patientService.patientService.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name="Patient",description = "API for patient management")
public class PatientController {
    @Autowired
    private final PatientService patientService;
    private final RecommendationStream stream;
    private final KafkaProducer kafkaProducer;

    public PatientController(PatientService patientService, RecommendationStream stream, KafkaProducer kafkaProducer) {
        this.patientService = patientService;
        this.stream = stream;
        this.kafkaProducer = kafkaProducer;
    }
    @GetMapping("/all")
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients()
    {
        List<PatientResponseDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @PostMapping("/savePatient")
    @Operation(summary = "Create a new patient", description = "Create a new patient with the provided details")
    public ResponseEntity<PatientResponseDTO> savePatient(@RequestBody @Valid PatientRequestDTO patientRequestDTO)
    {
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }


    @PutMapping("/updatePatient")
    @Operation(summary = "Update an existing patient", description = "Update the details of an existing patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@RequestBody @Valid PatientRequestDTO patientRequestDTO)
    {
        PatientResponseDTO patient = patientService.updatePatient(patientRequestDTO);
        return ResponseEntity.ok().body(patient);
    }

    @DeleteMapping("/deletePatient/{email}")
    @Operation(summary = "Delete a patient", description = "Delete a patient by their email address")
    public ResponseEntity<PatientResponseDTO> deletePatient(@PathVariable String email)
    {
        PatientResponseDTO patient = patientService.deletePatient(email);
        return ResponseEntity.ok().body(patient);
    }

    @GetMapping(value = "/recommendations", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamRecommendations(Patient patient) {
        kafkaProducer.askForHealthyFoods(patient.getPatientProblemDetails());
        return stream.getStream();
    }
 }
