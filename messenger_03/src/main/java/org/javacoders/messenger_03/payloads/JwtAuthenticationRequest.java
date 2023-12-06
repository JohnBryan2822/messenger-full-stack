package org.javacoders.messenger_03.payloads;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {
	
	private String username;
	private String password;
}
