package com.patientService.patientService.kafka;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;

@Component
public class RecommendationStream {

    private final Sinks.Many<String> sink;

    public RecommendationStream() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(String message) {
        sink.tryEmitNext(message);
    }

    public Flux<String> getStream() {
        return sink.asFlux();
    }
}
