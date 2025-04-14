package com.service.billing.grpc;

import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    public void createBillingAccount(billing.BillingRequest request, StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("createBillingAccount request Received: {}", request.toString());
        billing.BillingResponse response = billing.BillingResponse.newBuilder()
                .setAccountId("122")
                .setStatus("SUCCESS")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
