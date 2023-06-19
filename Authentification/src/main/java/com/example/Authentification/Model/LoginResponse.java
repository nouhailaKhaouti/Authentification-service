package com.example.Authentification.Model;

import lombok.Data;

@Data
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private String expire_in;
    private String refresh_expire_in;
    private String token_type;
}
