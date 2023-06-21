package com.codetech.authserver.controller;

import com.codetech.authserver.model.RegisterRequest;
import com.codetech.authserver.service.LoginService;
import com.codetech.authserver.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
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
        userservice.addUser(user);
        return "User Added Successfully.";
    }
    @GetMapping(path = "/{userName}")
    public List<UserRepresentation> getUser(@PathVariable("userName") String userName){
        List<UserRepresentation> user = userservice.getUser(userName);
        return user;
    }
    @PutMapping(path = "/update/{userId}")
    public String updateUser(@PathVariable("userId") String userId,   @RequestBody RegisterRequest user){
        userservice.updateUser(userId, user);
        return "User Details Updated Successfully.";
    }

    @DeleteMapping(path = "/{userId}")
    public String deleteUser(@PathVariable("userId") String userId){
        userservice.deleteUser(userId);
        return "User Deleted Successfully.";
    }
}
