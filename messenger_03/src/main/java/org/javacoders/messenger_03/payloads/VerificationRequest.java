package org.javacoders.messenger_03.payloads;

import lombok.Data;

@Data
public class VerificationRequest {
	
	private String code;
}
