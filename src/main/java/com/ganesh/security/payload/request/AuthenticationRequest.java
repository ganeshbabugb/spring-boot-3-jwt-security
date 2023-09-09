package com.ganesh.security.payload.request;

import com.ganesh.security.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @Email
    @Size(max = 50)
    private String email;

    @ValidPassword
    private String password;
}
