package com.patientService.patientService.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import signup.email.SignupEmailRequest;
import signup.email.SignupEmailResponse;
import signup.email.SignupEmailServiceGrpc;

@Service
@Slf4j
public class EmailServiceGrpcClient {

    private final SignupEmailServiceGrpc.SignupEmailServiceBlockingStub blockingStub;

    public EmailServiceGrpcClient(
            @Value("${email.service.address:email-service}") String grpcServerAddress,
            @Value("${email.service.grpc.port:9009}") int grpcServerPort) {

        log.info("Connecting to EMAIL gRPC service at {}:{}", grpcServerAddress, grpcServerPort);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcServerAddress, grpcServerPort)
                .usePlaintext()
                .build();

        this.blockingStub = SignupEmailServiceGrpc.newBlockingStub(channel);
    }

    public SignupEmailResponse sendEmail(String to, String patientId, String name, String mapJson) {
        log.info("Sending signup email to {}", to);

        SignupEmailRequest request = SignupEmailRequest.newBuilder()
                .setTo(to)
                .setPatientId(patientId)
                .setName(name)
                .setMap(mapJson)
                .build();

        SignupEmailResponse response = blockingStub.sendSignupEmail(request);

        log.info("Email service responded with: {}", response);
        return response;
    }
}
