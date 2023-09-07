package com.ganesh.security.controller;

import com.ganesh.security.payload.request.AuthenticationRequest;
import com.ganesh.security.payload.request.RegisterRequest;
import com.ganesh.security.payload.response.AuthenticationResponse;
import com.ganesh.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
            ) {
//            if (bindingResult.hasErrors()) return service.handleValidationErrors(bindingResult);
        AuthenticationResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
            ) {
//        if (bindingResult.hasErrors()) return service.handleValidationErrors(bindingResult);
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
