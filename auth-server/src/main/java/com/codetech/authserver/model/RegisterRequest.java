package com.codetech.authserver.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
