package com.ganesh.security.controller;

import com.ganesh.security.exceptions.DuplicateEmailException;
import com.ganesh.security.payload.request.AuthenticationRequest;
import com.ganesh.security.payload.request.RegisterRequest;
import com.ganesh.security.payload.response.AuthenticationResponse;
import com.ganesh.security.payload.response.MessageResponse;
import com.ganesh.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) return service.handleValidationErrors(bindingResult);
            AuthenticationResponse response = service.register(request);
            return ResponseEntity.ok(response);
        } catch (DuplicateEmailException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred on the server"));
        }
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) return service.handleValidationErrors(bindingResult);
            return ResponseEntity.ok(service.authenticate(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred on the server"));
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
}
