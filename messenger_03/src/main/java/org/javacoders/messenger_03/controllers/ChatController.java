package org.javacoders.messenger_03.controllers;

import java.util.List;

import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.ChatDto;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messenger/chats")
public class ChatController {
	
	private final ChatService chatService;
	
	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/create")
	public ResponseEntity<?> createChat(@RequestBody ChatDto chatDto){
		
		ChatDto newChat = this.chatService.createNewChat(chatDto);
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<UserDto>> getAvailableChatsForUser(@PathVariable Long userId){
		
		List<UserDto> contactsForUser = this.chatService.getChatsForUser(userId);
		
		return new ResponseEntity<List<UserDto>>(contactsForUser, HttpStatus.OK);
	}
}
