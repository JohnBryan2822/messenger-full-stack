package org.javacoders.messenger_03.services;

import java.util.List;

import org.javacoders.messenger_03.payloads.ChatDto;
import org.javacoders.messenger_03.payloads.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {
	
	ChatDto createNewChat(ChatDto chatDto);
	List<UserDto> getChatsForUser(Long userId);
}
