package com.codetech.authserver.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder()
				.serverUrl("http://localhost:8080/auth")
				.realm("lbv-realm")
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.clientId("auth-client")
				.clientSecret("VHqrOzv6X9QeenxlyOTBN8ZCbqusZubD")
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();
	}
}
