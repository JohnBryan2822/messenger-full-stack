package org.javacoders.messenger_03.controllers;

import java.util.List;

import org.javacoders.messenger_03.model.Message;
import org.javacoders.messenger_03.model.Notification;
import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.repository.UnreadMessageCountRepository;
import org.javacoders.messenger_03.services.ChatService;
import org.javacoders.messenger_03.services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messenger")
public class ChatController {
	
	private final ChatService chatService;
	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;
	
	@GetMapping("/chats/{userId}")
	public ResponseEntity<List<UserDto>> getAvailableChatsForUser(@PathVariable Long userId){
		
		List<UserDto> contactsForUser = this.chatService.getChatsForUser(userId);
		
		return new ResponseEntity<List<UserDto>>(contactsForUser, HttpStatus.OK);
	}
	
	@MessageMapping("/chat")
	public void processMessage(@Payload Message message) {
		
		System.out.println();
		System.out.println(message.getMessageText());
		System.out.println();
		
		Message savedMsg = this.messageService.saveNewMessage(message);
		
		int unreadMessageCount = this.messageService
				.updateUnreadMessageCount(message.getSender().getId(), message.getRecipient().getId());
		
		messagingTemplate.convertAndSendToUser(
				Long.toString(savedMsg.getRecipient().getId()), "/queue/messages",
				new Notification(
						savedMsg.getMessageId(),
						savedMsg.getSender().getId(),
						savedMsg.getRecipient().getId(),
						savedMsg.getMessageText(),
						unreadMessageCount));
	}
	
	@GetMapping("/messages/{senderId}/{recipientId}")
	public ResponseEntity<List<Message>> findChatMessages(
			@PathVariable Long senderId,
			@PathVariable Long recipientId){
		
		this.messageService.setUnreadMessageCountToZero(senderId, recipientId);
		
		return ResponseEntity.ok(this.messageService.getMessagesForChat(senderId, recipientId));
	}
	
//	/messages/{senderId}/{recipientId}/unreadCount
	@GetMapping("/messages/{senderId}/{recipientId}/unreadCount")
	public ResponseEntity<UnreadMessageCount> getUnreadMessageCount(
			@PathVariable Long senderId,
			@PathVariable Long recipientId) {
		
		UnreadMessageCount unreadMessageCount = this.messageService.getUnreadMessageCount(senderId, recipientId);
		return new ResponseEntity<UnreadMessageCount>(unreadMessageCount, HttpStatus.OK);
	}
}
