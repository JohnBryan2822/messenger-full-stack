package org.javacoders.messenger_03.services;

import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.UserDto;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface UserService {
	
	// Registration
	UserDto registerNewUser(User user);
	boolean sendVerificationToEmail(String username, String email);
	boolean verifyConfirmationCode(String email, String password);
	String extractCookieFromRequest(HttpServletRequest request, String cookieName);
	
}
