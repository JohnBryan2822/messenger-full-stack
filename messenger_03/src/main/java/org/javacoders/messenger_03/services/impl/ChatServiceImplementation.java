package org.javacoders.messenger_03.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.ChatDto;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.repository.ChatRepository;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImplementation implements ChatService {
	
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public ChatServiceImplementation(ChatRepository chatRepository, UserRepository userRepository,
			ModelMapper modelMapper) {
		this.chatRepository = chatRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public ChatDto createNewChat(ChatDto chatDto) {
		
		User user1 = this.userRepository.findById(chatDto.getUser1()).orElseThrow();
		User user2 = this.userRepository.findById(chatDto.getUser2()).orElseThrow();
		
		Chat chat = new Chat(null, user1, user2, new Date(), null);
		this.chatRepository.save(chat);
		
		return chatDto;
	}

	@Override
	public List<UserDto> getChatsForUser(Long userId) {
		
		User user1 = this.userRepository.findById(userId).orElseThrow();
		List<Chat> chats = this.chatRepository.findByUser1(user1);
		List<User> users = chats.stream().map(Chat::getUser2).collect(Collectors.toList());
		
		return users
				.stream()
				.map(user -> this.modelMapper.map(user, UserDto.class))
				.collect(Collectors.toList());
	}
	
}
