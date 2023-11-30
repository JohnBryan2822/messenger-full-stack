package org.javacoders.messenger_03.services;

import org.javacoders.messenger_03.payloads.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
	
	// Registration
	UserDto registerNewUser(UserDto userDto);
	boolean sendVerificationToEmail(String email);
	boolean verifyConfirmationCode(String email, String password);
	
	// users

}
