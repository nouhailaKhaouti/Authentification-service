package com.codetech.authserver.controller;

import com.codetech.authserver.model.RegisterRequest;
import com.codetech.authserver.service.LoginService;
import com.codetech.authserver.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.representations.UserInfo;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userservice;
    @Autowired
    BearerTokenResolver bearerTokenResolver;

    @PostMapping("/")
    public String addUser(@RequestBody RegisterRequest user){
        userservice.createUser(user);
        return "User Added Successfully.";
    }
    @GetMapping(path = "/{userName}")
    public List<UserRepresentation> getUser(@PathVariable("userName") String userName){
        List<UserRepresentation> user = userservice.getUser(userName);
        return user;
    }
    @GetMapping(path = "/users")
    public List<UserRepresentation> getUsers(){
        List<UserRepresentation> user = userservice.getUsers();
        return user;
    }
    @GetMapping(path = "/users/enable")
    public List<UserRepresentation> getUsersEnabled(){
        List<UserRepresentation> user = userservice.getUsersEnabled();
        return user;
    }
    @GetMapping(path = "/users/email")
    public List<UserRepresentation> findByEmail(@RequestParam("email") String email){
        List<UserRepresentation> user = userservice.findByEmail(email);
        return user;
    }
    @PutMapping(path = "/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") String userId,   @RequestBody RegisterRequest user){
        return userservice.updateUser(userId, user);
    }
    @DeleteMapping(path = "/{userId}")
//    @PreAuthorize("hasRole('PROF')")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId){

        return userservice.deleteUser(userId);
    }
    @GetMapping("/info")
    public ResponseEntity<?> userinfo( @RequestParam("token") String token){
        return userservice.verifyToken(token);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        return userservice.initiatePasswordReset(email);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody String password,@RequestParam("token") String token) {
        return userservice.resetPassword(token,password);
    }
}
