package com.analytics.ai.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient",groupId = "analytics-service")
    public void consumeEvent(byte[] event)  {
        try{
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received event: {}", patientEvent.getEmail(),patientEvent.getName());


        }
        catch (Exception e)
        {
            log.error("Error while consuming message from Kafka: {}", e.getMessage());
        }
    }
}
