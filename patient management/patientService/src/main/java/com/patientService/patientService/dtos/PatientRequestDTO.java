package com.patientService.patientService.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatientRequestDTO {

    @NotNull(message = "patientName cannot be null")
    @Size(min = 2, max = 50, message = "patientName must be between 2 and 50 characters")
    private String patientName;

    @NotNull(message = "email address cannot be null")
    private String email;

    @NotNull(message = "address cannot be null")
    private String address;

    @NotNull(message = "dateOfBirth cannot be null")
    private String dateOfBirth;

    @NotBlank(message = "registeredDate cannot be null")
    private String registeredDate;
}
