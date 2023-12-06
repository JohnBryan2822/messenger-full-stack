package org.javacoders.messenger_03.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javacoders.messenger_03.model.Notification;
import org.javacoders.messenger_03.payloads.MessageDto;
import org.javacoders.messenger_03.payloads.NewMessage;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.security.JwtTokenHelper;
import org.javacoders.messenger_03.services.ChatService;
import org.javacoders.messenger_03.services.MessageService;
import org.javacoders.messenger_03.services.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ChatController {
	
	private final ChatService chatService;
	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;
	private final JwtTokenHelper jwtTokenHelper;
	private final UserService userService;
	private RedisTemplate<String, String> redisTemplate;
	
	public ChatController(ChatService chatService, MessageService messageService,
			SimpMessagingTemplate messagingTemplate, JwtTokenHelper jwtTokenHelper, UserService userService,
			RedisTemplate<String, String> redisTemplate) {
		super();
		this.chatService = chatService;
		this.messageService = messageService;
		this.messagingTemplate = messagingTemplate;
		this.jwtTokenHelper = jwtTokenHelper;
		this.userService = userService;
		this.redisTemplate = redisTemplate;
	}

	@GetMapping("/messenger/chats")
	public ResponseEntity<List<UserDto>> getAvailableChatsForUser(
			HttpServletRequest request){
		
		String token = this.userService.extractCookieFromRequest(request, "jwtToken");
		Long userId = this.jwtTokenHelper.getIdFromToken(token);
		
		String sessionId = this.userService.extractCookieFromRequest(request, "sessionId");
		System.out.println("extracting session Id : " + sessionId);
		Long sessionUserId = Long.parseLong(this.redisTemplate.opsForValue().get(sessionId));
		
		if(sessionUserId == null || sessionUserId != userId) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
		}
		
		List<UserDto> contactsForUser = this.chatService.getChatsForUser(userId);
		return new ResponseEntity<List<UserDto>>(contactsForUser, HttpStatus.OK);
	}
	
//	/user/2/queue/messages
	@MessageMapping("/chat")
	public void processMessage(@Payload NewMessage message) {
		
		System.out.println(message.getMessageText());
		
		MessageDto messageDto = MessageDto.builder()
				.senderId(message.getSenderId())
				.recipientId(message.getRecipientId())
				.messageText(message.getMessageText())
				.timestamp(new Date())
				.build();
		this.messageService.saveNewMessage(messageDto);
		messagingTemplate.convertAndSendToUser(
				Long.toString(message.getRecipientId()), "/queue/messages",
				message);
	}
	
	@GetMapping("/messenger/messages/{userId}")
	public ResponseEntity<List<MessageDto>> findChatMessages(
			@PathVariable Long userId,
			HttpServletRequest request){
		
		String token = this.userService.extractCookieFromRequest(request, "jwtToken");
		
		Long myId = this.jwtTokenHelper.getIdFromToken(token);
		
		return ResponseEntity.ok(this.messageService.getMessagesForChat(myId, userId));
	}
}
