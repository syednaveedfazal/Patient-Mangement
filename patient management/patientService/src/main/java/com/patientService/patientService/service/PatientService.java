package com.patientService.patientService.service;

import com.patientService.patientService.dtos.PatientRequestDTO;
import com.patientService.patientService.entities.Patient;
import com.patientService.patientService.dtos.PatientResponseDTO;
import com.patientService.patientService.exception.EmailAlreadyExistException;
import com.patientService.patientService.exception.PatientNotFoundException;
import com.patientService.patientService.grpc.BillingServiceGrpcClient;
import com.patientService.patientService.grpc.EmailServiceGrpcClient;
import com.patientService.patientService.kafka.KafkaProducer;
import com.patientService.patientService.mapper.PatientToResponseDTO;
import com.patientService.patientService.repostiory.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PatientService {


    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    private final EmailServiceGrpcClient emailServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer, EmailServiceGrpcClient emailServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
        this.emailServiceGrpcClient = emailServiceGrpcClient;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientToResponseDTO::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistException("Email already exists");
        }
        Patient newPatient = patientRepository.save(PatientToResponseDTO.toModel(patientRequestDTO));
//        try {
            billingServiceGrpcClient.createBillingAccount(newPatient.getPatientId().toString(), newPatient.getPatientName(), newPatient.getEmail());
            emailServiceGrpcClient.sendEmail(patientRequestDTO.getEmail(),"122",patientRequestDTO.getPatientName(),"ss"); //.sendEmail(patientRequestDTO.getEmail(), patientRequestDTO.getPatientName());
            kafkaProducer.sendMessage(newPatient);
//        } catch (Exception e) {
//            log.warn(e.getMessage().toString());
//        }

        return PatientToResponseDTO.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(PatientRequestDTO patientRequestDTO) {
        Patient updatedPatient = patientRepository.findByEmail(patientRequestDTO.getEmail())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        updatedPatient.setPatientName(patientRequestDTO.getPatientName());
        updatedPatient.setAddress(patientRequestDTO.getAddress());
        updatedPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        updatedPatient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        patientRepository.save(updatedPatient);
        return PatientToResponseDTO.toDTO(updatedPatient);
    }

    public PatientResponseDTO deletePatient(String email) {
        Patient deletedPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));
        patientRepository.delete(deletedPatient);
        return PatientToResponseDTO.toDTO(deletedPatient);
    }


}
