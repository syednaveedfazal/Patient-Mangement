package com.patientService.patientService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PatientProblemDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String problemID;
    @Column(nullable = false)
    private String problemDescription;

    @Column(nullable = false)
    private String severity;

    @OneToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "patientId")
    private Patient patient;

}
