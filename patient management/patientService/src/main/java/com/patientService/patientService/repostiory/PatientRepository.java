package com.patientService.patientService.repostiory;

import com.patientService.patientService.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByEmail(String email);

    Optional<Patient> findByEmail(String email);
}