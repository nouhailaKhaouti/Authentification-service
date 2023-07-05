package com.codetech.authserver.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;

    public UserServiceTest() {
    }
   @Test
    public void getUsers() {
       List<UserRepresentation> users = userService.getUsers();
       assertNotNull(users);
       assertFalse(users.isEmpty());
    }

    @Test
    public void getUsersEnabled() {
        List<UserRepresentation> users = userService.getUsersEnabled();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}