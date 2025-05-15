package com.patientService.patientService.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class KafkaAIConsumer {

    private final RecommendationStream stream;

    public KafkaAIConsumer(RecommendationStream stream) {
        this.stream = stream;
    }

    @KafkaListener(topics = "patient-recommendation", groupId = "analytics-service")
    public void consume(String message) {
        stream.publish(message);
        log.info("Received: {}", message);
    }
}
