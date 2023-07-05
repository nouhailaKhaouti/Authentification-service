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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userservice;
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
    @PutMapping(path = "/update/{userId}")
    public String updateUser(@PathVariable("userId") String userId,   @RequestBody RegisterRequest user){
        userservice.updateUser(userId, user);
        return "User Details Updated Successfully.";
    }
    @DeleteMapping(path = "/{userId}")
//    @PreAuthorize("hasRole('PROF')")
    public String deleteUser(@PathVariable("userId") String userId){
        userservice.deleteUser(userId);
        return "User Deleted Successfully.";
    }
    @GetMapping("/info")
    public ResponseEntity<?> userinfo(HttpServletRequest request){
        return userservice.verifyToken(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        boolean success = userservice.initiatePasswordReset(email);
        if (success) {
            return ResponseEntity.ok("Password reset email sent");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate password reset");
        }
    }
}
