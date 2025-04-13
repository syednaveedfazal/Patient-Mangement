package com.patientService.patientService.mapper;

import com.patientService.patientService.dtos.PatientRequestDTO;
import com.patientService.patientService.entities.Patient;
import com.patientService.patientService.dtos.PatientResponseDTO;

import java.time.LocalDate;

public class PatientToResponseDTO {

    public static PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setPatientId(patient.getPatientId().toString());
        patientResponseDTO.setName(patient.getPatientName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setAddress(patient.getAddress());
        patientResponseDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientResponseDTO;
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setPatientName(patientRequestDTO.getPatientName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
}
