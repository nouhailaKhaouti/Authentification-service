package com.codetech.authserver.service;

import com.codetech.authserver.config.Credentials;
import com.codetech.authserver.model.RegisterRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.codetech.authserver.config.KeycloakConfig.getInstance;

@Service
public class UserService {
    public void addUser(RegisterRequest userrequest){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userrequest.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userrequest.getUsername());
        user.setFirstName(userrequest.getFirstname());
        user.setLastName(userrequest.getLastname());
        user.setEmail(userrequest.getEmail());
        List<CredentialRepresentation> credentials = new ArrayList<>();
        credentials.add(credential);
        user.setCredentials(credentials);
        user.setEnabled(true);
        Keycloak keycloak = getInstance();
        RealmResource realmResource = keycloak.realm("lbv-realm");
        UsersResource usersResource = realmResource.users();
        usersResource.create(user);
    }
    public List<UserRepresentation> getUser(String userName){
        Keycloak keycloak = getInstance();
        RealmResource realmResource = keycloak.realm("lbv-realm");
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> user = usersResource.search(userName, true);
        return user;
    }
    public void updateUser(String userId, RegisterRequest userrequest){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userrequest.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userrequest.getUsername());
        user.setFirstName(userrequest.getFirstname());
        user.setLastName(userrequest.getLastname());
        user.setEmail(userrequest.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        Keycloak keycloak = getInstance();
        RealmResource realmResource = keycloak.realm("lbv-realm");
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId).update(user);
    }
    public void deleteUser(String userId){
        Keycloak keycloak = getInstance();
        RealmResource realmResource = keycloak.realm("lbv-realm");
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId)
                .remove();
    }
}
