package com.analytics.ai.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumer {
    private ChatClient chatClient;
    private KafkaTemplate<String,Flux> kafkaTemplate;
    public KafkaConsumer(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

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


    @KafkaListener(topics = "patient-recommendation", groupId = "analytics-service")
    public void consumeHistoryEvent() {
        log.info("in something: {}");
        kafkaTemplate.send("patient-recommendation",chatClient.prompt()
                .user("Give Some healthy eating foods for the user to improve health based on this details ")
                .stream()
                .content());
    }
}
