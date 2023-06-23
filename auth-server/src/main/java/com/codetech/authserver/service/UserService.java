package com.codetech.authserver.service;

import com.codetech.authserver.config.Credentials;
import com.codetech.authserver.model.RegisterRequest;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.keycloak.admin.client.Keycloak.getInstance;

@Service
public class UserService {
    private final Keycloak keycloak;
    private final String realmName;

    @Autowired
    public UserService(Keycloak keycloak, @Value("lbv-realm") String realmName) {
        this.keycloak = keycloak;
        this.realmName = realmName;
    }

    public void createUser(RegisterRequest userDto) {
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstname());
        user.setLastName(userDto.getLastname());
        user.setEnabled(true);
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            String responseBody = response.readEntity(String.class);
            System.out.println("User creation response body: " + responseBody);
            throw new RuntimeException("Failed to create user");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.println("Created user with ID: " + userId);
    }
    public List<UserRepresentation> getUser(String userName){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> user = usersResource.search(userName, true);
        return user;
    }
    public void updateUser(String userId, RegisterRequest userrequest){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userrequest.getPassword());
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userrequest.getUsername());
        user.setFirstName(userrequest.getFirstname());
        user.setLastName(userrequest.getLastname());
        user.setEmail(userrequest.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        usersResource.get(userId).update(user);
    }
    public void deleteUser(String userId){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId)
                .remove();
    }
}
