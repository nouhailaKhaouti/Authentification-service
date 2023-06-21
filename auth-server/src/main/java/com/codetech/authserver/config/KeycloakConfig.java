package com.codetech.authserver.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class KeycloakConfig {

	@Bean
	public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}
	static Keycloak keycloak = null;
	final static String serverUrl = "http://localhost:8080/auth";
	public final static String realm = "lbv";
	final static String clientId = "auth-client";
	final static String clientSecret = "VHqrOzv6X9QeenxlyOTBN8ZCbqusZubD";
	final static String userName = "admin";
	final static String password = "admin";
	public static Keycloak getInstance() {
		if (keycloak == null) {
			keycloak = KeycloakBuilder.builder()
					.serverUrl(serverUrl)
					.realm(realm)
					.grantType(OAuth2Constants.PASSWORD)
					.username(userName)
					.password(password)
					.clientId(clientId)
					.clientSecret(clientSecret)
					.resteasyClient(new ResteasyClientBuilder()
							.connectionPoolSize(10)
							.build())
					.build();
		}
		return keycloak;
	}
}
