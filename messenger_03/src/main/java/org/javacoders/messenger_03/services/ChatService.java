package org.javacoders.messenger_03.services;

import java.util.List;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.payloads.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
	
	List<UserDto> getChatsForUser(Long userId);
	Chat getChat(Long senderId, Long recipientId, boolean shouldCreate);
}
