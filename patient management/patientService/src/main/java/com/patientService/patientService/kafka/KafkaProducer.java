package com.patientService.patientService.kafka;

import com.patientService.patientService.entities.Patient;
import com.patientService.patientService.entities.PatientProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import problem.details.ProblemDetails;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaTemplate<String, Flux> kafkaAiTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate, KafkaTemplate<String, Flux> kafkaAiTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAiTemplate = kafkaAiTemplate;
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
    public void askForHealthyFoods(PatientProblemDetails patientProblemDetails)
    {
//        ProblemDetails problemDetail = ProblemDetails.newBuilder()
//                .setProblemId(patientProblemDetails.getProblemID())
//                .setProblemDescription(patientProblemDetails.getProblemDescription())
//                .setSeverity(patientProblemDetails.getSeverity())
//                .build();
        try{
            kafkaAiTemplate.send("patient-recommendation",Flux.empty());
        }
        catch (Exception e)
        {
            log.warn("Error while sending message to Kafka recommand: {}", e.getMessage());

        }

    }
}
