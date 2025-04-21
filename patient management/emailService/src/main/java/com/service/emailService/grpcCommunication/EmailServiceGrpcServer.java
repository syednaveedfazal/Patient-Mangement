package com.service.emailService.grpcCommunication;

import io.grpc.stub.StreamObserver;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import signup.email.SignupEmailRequest;
import signup.email.SignupEmailResponse;
import signup.email.SignupEmailServiceGrpc;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@GrpcService
public class EmailServiceGrpcServer extends SignupEmailServiceGrpc.SignupEmailServiceImplBase {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendSignupEmail(SignupEmailRequest request, StreamObserver<SignupEmailResponse> responseObserver) {
        String to = request.getTo();
        String patientId = request.getPatientId();
        String name = request.getName();

        log.info("📨 Sending email to: {}, patientId: {}, name: {}", to, patientId, name);

        try {
            // Build context from gRPC request
            Map<String, Object> model = new HashMap<>();
            model.put("name", name);
            model.put("patientId", patientId);

            Context context = new Context();
            context.setVariables(model);

            String htmlContent = templateEngine.process("email-template", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setSubject("Welcome Email " + patientId);
            helper.setText(htmlContent, true);
            helper.setFrom("syednaveedfazal123@gmail.com");

            mailSender.send(mimeMessage);

            SignupEmailResponse response = SignupEmailResponse.newBuilder()
                    .setStatus("SUCCESS")
                    .setMessage("Email sent successfully to " + to)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("❌ Failed to send email", e);
            SignupEmailResponse response = SignupEmailResponse.newBuilder()
                    .setStatus("FAILURE")
                    .setMessage("Failed to send email: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
