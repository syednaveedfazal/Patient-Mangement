package com.patientService.patientService.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PatientResponseDTO {

    private String patientId;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
}
