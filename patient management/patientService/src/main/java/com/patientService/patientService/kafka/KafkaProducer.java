package com.patientService.patientService.kafka;

import com.patientService.patientService.entities.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;


    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Patient patient) {
        PatientEvent event = PatientEvent.newBuilder().
                setEventType("PATIENT_CREATED").
                setEmail(patient.getEmail())
                .setName(patient.getPatientName())
                .setPatientId(patient.getPatientId().toString())
                .build();
        try {
            kafkaTemplate.send("patient", event.toByteArray());
        } catch (Exception e) {
            log.warn("Error while sending message to Kafka: {}", e.getMessage());
        }
    }
}
