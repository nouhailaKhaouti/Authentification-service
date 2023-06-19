package com.example.Authentification;
import com.example.Authentification.Model.LoginRequest;
import com.example.Authentification.Model.LoginResponse;
import com.example.Authentification.Service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    final LoginService loginservice
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest loginrequest) {
        return loginservice.login(loginrequest);
    }
}
