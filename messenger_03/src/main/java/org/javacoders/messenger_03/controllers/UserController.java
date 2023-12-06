package org.javacoders.messenger_03.controllers;

import java.util.List;

import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/messenger/search-user")
	public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String username){
		List<UserDto> searchResult = this.userService.searchUsersByUsername(username);
		return new ResponseEntity<List<UserDto>>(searchResult, HttpStatus.OK);
	}
}
