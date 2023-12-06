package org.javacoders.messenger_03.exceptions;

public class ApiException extends RuntimeException {
	
	public ApiException(String message) {
		super(message);
	}
	
	public ApiException() {
		super();
	}
}
