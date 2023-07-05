package com.codetech.authserver.service;



import com.codetech.authserver.config.Credentials;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.springframework.http.*;
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
import org.springframework.web.client.RestTemplate;
import javax.ws.rs.core.Response;
import java.util.*;


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
    public List<UserRepresentation> getUsers(){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> users = usersResource.list();
        return users;
    }
    public List<UserRepresentation> getUsersEnabled(){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> allUsers = usersResource.list();
        List<UserRepresentation> users = new ArrayList<>();
        for (UserRepresentation user : allUsers) {
            if (user.isEnabled()) {
                users.add(user);
            }
        }
        return users;
    }
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String bearerTokenNew = bearerToken.substring(7);

            String keycloakUrl = "http://localhost:8080/auth/realms/lbv-realm/protocol/openid-connect/userinfo";

            if (bearerTokenNew != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(bearerTokenNew);

                HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                RestTemplate restTemplate = new RestTemplate();

                try {
                    ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.GET, requestEntity, String.class);
                    HttpStatusCode statusCode = response.getStatusCode();
                    if (statusCode.is2xxSuccessful()) {
                        return ResponseEntity.status(HttpStatus.OK).body(true);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
                    }
                } catch (Exception ex) {
                    // Handle other exceptions
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
    public void updateUser(String userId, RegisterRequest userRequest){

        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userRequest.getPassword());
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
//        List<UserRepresentation> userF = usersResource.search(userRequest.getUsername(), true);
//        if (userF.isEmpty()) {
//
//        }
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstname());
        user.setLastName(userRequest.getLastname());
        user.setEmail(userRequest.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        usersResource.get(userId).update(user);
    }
    public void deleteUser(String userId){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId)
                .remove();
    }
    public boolean initiatePasswordReset(String email) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> users = usersResource.search(email);
            if (users.isEmpty()) {
                return false;
            }
            String userId = users.get(0).getId();
            UserResource userResource = usersResource.get(userId);
            userResource.executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"), null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
