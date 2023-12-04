package org.javacoders.messenger_03.controllers;

import java.util.List;

import org.javacoders.messenger_03.model.Message;
import org.javacoders.messenger_03.model.Notification;
import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.javacoders.messenger_03.payloads.MessageDto;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.services.ChatService;
import org.javacoders.messenger_03.services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatService chatService;
	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;
	
	@GetMapping("/messenger/chats/{userId}")
	public ResponseEntity<List<UserDto>> getAvailableChatsForUser(@PathVariable Long userId){
		
		List<UserDto> contactsForUser = this.chatService.getChatsForUser(userId);
		
		return new ResponseEntity<List<UserDto>>(contactsForUser, HttpStatus.OK);
	}
	
	@MessageMapping("/chat")
	public void processMessage(@Payload MessageDto message) {
		System.out.println();
		System.out.println(message.getMessageText());
		MessageDto savedMsg = this.messageService.saveNewMessage(message);
		
		int unreadMessageCount = this.messageService
				.updateUnreadMessageCount(message.getSenderId(), message.getRecipientId());
		
		messagingTemplate.convertAndSendToUser(
				Long.toString(savedMsg.getRecipientId()), "/queue/messages",
				new Notification(
						savedMsg.getMessageId(),
						savedMsg.getSenderId(),
						savedMsg.getRecipientId(),
						savedMsg.getMessageText(),
						unreadMessageCount));
	}
	
	@GetMapping("/messenger/messages/{myId}/{userId}")
	public ResponseEntity<List<MessageDto>> findChatMessages(
			@PathVariable Long myId,
			@PathVariable Long userId){
		this.messageService.setUnreadMessageCountToZero(myId, userId);
		
		return ResponseEntity.ok(this.messageService.getMessagesForChat(myId, userId));
	}
	
//	/messages/{senderId}/{recipientId}/unreadCount
	@GetMapping("/messenger/messages/{senderId}/{recipientId}/unreadCount")
	public ResponseEntity<UnreadMessageCount> getUnreadMessageCount(
			@PathVariable Long senderId,
			@PathVariable Long recipientId) {
		
		UnreadMessageCount unreadMessageCount = this.messageService.getUnreadMessageCount(senderId, recipientId);
		return new ResponseEntity<UnreadMessageCount>(unreadMessageCount, HttpStatus.OK);
	}
}
