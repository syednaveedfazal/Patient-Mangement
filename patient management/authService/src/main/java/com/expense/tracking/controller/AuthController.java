package com.expense.tracking.controller;

import com.expense.tracking.entities.RefreshToken;
import com.expense.tracking.model.UserInfoDTO;
import com.expense.tracking.response.JwtResponseDTO;
import com.expense.tracking.services.EmailService;
import com.expense.tracking.services.JwtService;
import com.expense.tracking.services.RefreshTokenService;
import com.expense.tracking.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;

    public boolean sendEmail(String recipientEmail) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Syed");
        model.put("title", "Welcome!");
        model.put("body", "Thanks for signing up. Enjoy our service!");

        emailService.sendTemplateMail(recipientEmail, "Welcome Email", model);

        return true;
    }

    @PostMapping("/auth/v1/signup")
    @Operation(summary = "Sign up endpoint")
    public ResponseEntity SignUp(@RequestBody UserInfoDTO userInfoDto) {
        try {
            Boolean isSignUped = userDetailsService.signUpUser(userInfoDto);
            if (Boolean.FALSE.equals(isSignUped)) {
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUserName());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUserName());
            sendEmail(userInfoDto.getEmail());//sendTemplateMail(userInfoDto.getEmail());
            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).
                    token(refreshToken.getToken()).build(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/validate")
    @Operation(summary = "validation Endpoint")

    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        log.info("in validateToken, authHeader: {}", authHeader);
        String token = authHeader.substring(7);
        String username = jwtService.extractUserName(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
