package com.codetech.authserver.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.codetech.authserver.model.LoginRequest;
import com.codetech.authserver.model.LoginResponse;

@Service
public class LoginService {

	@Autowired
	RestTemplate restTemplate;

	public ResponseEntity<LoginResponse> login(LoginRequest loginrequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("client_id","auth-client" );
		map.add("client_secret", "FqCNVMHcpNIrpix77bTFTRsph6vkSKsl");
		map.add("grant_type", "password");
		map.add("username", loginrequest.getUsername());
		map.add("password", loginrequest.getPassword());

		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map,headers);

		ResponseEntity<LoginResponse> response = restTemplate.postForEntity("http://localhost:8080/auth/realms/lbv/protocol/openid-connect/token", httpEntity, LoginResponse.class);
		return new ResponseEntity<>(response.getBody(),HttpStatus.OK);


	}




}
