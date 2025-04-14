package com.patientService.patientService.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {

    public final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String grpcServerAddress,
            @Value("${billing.service.grpc.port:9001}") int grpcServerPort) {
        log.info("Connecting to billing servie at {}:{}", grpcServerAddress, grpcServerPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcServerAddress,grpcServerPort)
                .usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }
    public billing.BillingResponse createBillingAccount(String patientId,String name,String email) {
        log.info("createBillingAccount request: {}", patientId.toString());
        billing.BillingRequest request = BillingRequest.newBuilder().setEmail(email).setName(name).setPatientId(patientId).build();
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("createBillingAccount response: {}", response.toString());
        return response;
    }
}
