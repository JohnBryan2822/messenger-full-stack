package org.javacoders.messenger_03.controllers;

import java.util.List;

import org.javacoders.messenger_03.model.Notification;
import org.javacoders.messenger_03.payloads.MessageDto;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.security.JwtTokenHelper;
import org.javacoders.messenger_03.services.ChatService;
import org.javacoders.messenger_03.services.MessageService;
import org.javacoders.messenger_03.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatService chatService;
	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;
	private final JwtTokenHelper jwtTokenHelper;
	private final UserService userService;
	
	@GetMapping("/messenger/chats")
	public ResponseEntity<List<UserDto>> getAvailableChatsForUser(
			HttpServletRequest request){
		
		String token = this.userService.extractJwtFromRequest(request);
		Long userId = this.jwtTokenHelper.getIdFromToken(token);
		List<UserDto> contactsForUser = this.chatService.getChatsForUser(userId);
		return new ResponseEntity<List<UserDto>>(contactsForUser, HttpStatus.OK);
	}
	
//	/user/2/queue/messages
	@MessageMapping("/chat")
	public void processMessage(@Payload MessageDto message,
			HttpServletRequest request) {
		String token = this.userService.extractJwtFromRequest(request);
		Long userId = this.jwtTokenHelper.getIdFromToken(token);
		message.setSenderId(userId);
		MessageDto savedMsg = this.messageService.saveNewMessage(message);
		
		messagingTemplate.convertAndSendToUser(
				Long.toString(savedMsg.getRecipientId()), "/queue/messages",
				new Notification(
						savedMsg.getMessageId(),
						savedMsg.getSenderId(),
						savedMsg.getRecipientId(),
						savedMsg.getMessageText()));
	}
	
	@GetMapping("/messenger/messages/{userId}")
	public ResponseEntity<List<MessageDto>> findChatMessages(
			@PathVariable Long userId,
			HttpServletRequest request){
		
		String token = this.userService.extractJwtFromRequest(request);
		
		Long myId = this.jwtTokenHelper.getIdFromToken(token);
		
		return ResponseEntity.ok(this.messageService.getMessagesForChat(myId, userId));
	}
}
