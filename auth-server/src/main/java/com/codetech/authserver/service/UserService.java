package com.codetech.authserver.service;

import com.codetech.authserver.config.EmailConfig;
import com.codetech.authserver.config.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.resource.UserResource;
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

import static com.codetech.authserver.config.Credentials.createPasswordCredentials;


@Service
public class UserService {
    private final Keycloak keycloak;
    private final String realmName;

    private final JwtTokenUtil jwtTokenUtil;

    private final EmailConfig emailConfig;


    @Autowired
    public UserService(Keycloak keycloak, @Value("lbv-realm") String realmName,EmailConfig emailConfig, JwtTokenUtil jwtTokenUtil) {
        this.keycloak = keycloak;
        this.realmName = realmName;
        this.emailConfig=emailConfig;
        this.jwtTokenUtil=jwtTokenUtil;
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
        if(usersResource.list().isEmpty()) throw new RuntimeException("test");
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
    public ResponseEntity<?> verifyToken(String token) {
            String keycloakUrl = "http://localhost:8080/auth/realms/lbv-realm/protocol/openid-connect/userinfo";
            if (token != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
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
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
    }
    public List<UserRepresentation> findByEmail(String email){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> allUsers = usersResource.list();
        List<UserRepresentation> matchingUsers = new ArrayList<>();

        for (UserRepresentation user : allUsers) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                matchingUsers.add(user);
            }
        }
        return matchingUsers;
    }
    public ResponseEntity<String> updateUser(String userId, RegisterRequest userRequest){

        CredentialRepresentation credential = createPasswordCredentials(userRequest.getPassword());
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> userID=usersResource.search(userId);
        if(userID.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Update user can't find a user with id: "+userId);
        }
            UserRepresentation user = new UserRepresentation();
            user.setUsername(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstname());
            user.setLastName(userRequest.getLastname());
            user.setEmail(userRequest.getEmail());
            user.setCredentials(Collections.singletonList(credential));
            usersResource.get(userId).update(user);
        return ResponseEntity.ok("The user with the id:"+userId+"is Updated successfully");
    }
    public ResponseEntity<String> deleteUser(String userId){
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> user=usersResource.search(userId);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Delete user can't find a user with id: "+userId);
        }else{
        usersResource.get(userId)
                .remove();
            return ResponseEntity.ok("The user with the id:"+userId+"is Deleted successfully");
        }

    }
    public ResponseEntity<?> initiatePasswordReset(String email) {
        RealmResource realmResource = keycloak.realm(realmName);
        List<UserRepresentation> users = findByEmail(email);
        //check the user existence
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // get user id
        UserRepresentation user = users.get(0);
        String userId = user.getId();
//        //remove old password
//        CredentialRepresentation credential=new CredentialRepresentation();
//        credential.setType(CredentialRepresentation.PASSWORD);
//        credential.setValue(null);
//        UserResource userResource = realmResource.users().get(userId);
//        try {
//            userResource.resetPassword(credential);
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("can't reset password.");
//        }
        //call generateToken methode to generate a new token with email and expiration date
        String token = jwtTokenUtil.generateToken(email);
        UsersResource usersResource = realmResource.users();
        UserRepresentation userToken = user;
        Map<String, List<String>> attributes = userToken.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
            userToken.setAttributes(attributes);
        }
        attributes.put("custom_token", Collections.singletonList(token));
        usersResource.get(userId).update(userToken);
        //send email verification to user email with the reset link attached to the generated token
        String verificationLink = "http://localhost:8090/user/reset-password?token=" + token;
        emailConfig.sendVerificationEmail(email, verificationLink);
        return ResponseEntity.ok("Password reset link sent successfully.+ this the token for reset password:"+token);
    }
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        try {
            //verify the expiration date of the token
        if (jwtTokenUtil.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }
        //retrieve user email from token
        List<UserRepresentation> users=findByEmail(jwtTokenUtil.getEmailFromToken(token));
        // get "custom_token" value from keycloak to compare with the request token
        UserRepresentation user = users.get(0);
            Map<String, List<String>> attributes = user.getAttributes();
            List<String> customTokenValues = attributes.get("custom_token");
            String customToken = (customTokenValues != null && !customTokenValues.isEmpty()) ? customTokenValues.get(0) : null;
       //verify token  by comparing the two token , keycloakToken and requestToken
        if(!jwtTokenUtil.verifyToken(customToken,token)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token Invalid");
        }
        //update password with the new password
        RealmResource realmResource = keycloak.realm(realmName);
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(user.getId());
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        userResource.resetPassword(credential);
        //remove token from keycloak
        user.getAttributes().remove("custom_token");
        userResource.update(user);
        return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed to update the Password");
        }
    }

}
