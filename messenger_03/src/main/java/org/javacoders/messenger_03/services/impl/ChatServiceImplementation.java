package org.javacoders.messenger_03.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.repository.ChatRepository;
import org.javacoders.messenger_03.repository.MessageRepository;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.services.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImplementation implements ChatService {
	
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final MessageRepository messageRepository;

	@Override
	public List<UserDto> getChatsForUser(Long userId) {
		
		User sender = this.userRepository.findById(userId).orElseThrow();
		List<Chat> chats = this.chatRepository.findBySender(sender);
		List<Chat> chats2 = this.chatRepository.findByRecipient(sender);
		List<User> users = chats.stream().map(Chat::getRecipient).collect(Collectors.toList());
		List<User> users2 = chats2.stream().map(Chat::getSender).collect(Collectors.toList());
		users.addAll(users2);
		return users
				.stream()
				.map(user -> this.modelMapper.map(user, UserDto.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public Chat getChat(Long senderId, Long recipientId) {
		if(senderId == recipientId) {
			throw new RuntimeException("Sender and recipient cannot be the same");
		}
		
		String chatId = senderId + "_" + recipientId;
		if(senderId > recipientId)
			chatId = recipientId + "_" + senderId;
			
		return this.chatRepository.findById(chatId).orElse(createChat(senderId, recipientId));

//		return this.messageRepository
//				.findBySenderAndRecipient(sender, recipient)
//				.orElse(createChat(sender, recipient));
	}

	private Chat createChat(Long senderId, Long recipientId) {
		
		String chatId = senderId + "_" + recipientId;
		if(senderId > recipientId)
			chatId = recipientId + "_" + senderId;
		
		User user1 = this.userRepository.findById(senderId).orElseThrow();
		User user2 = this.userRepository.findById(recipientId).orElseThrow();
		
		Chat chat = this.chatRepository.save(new Chat(chatId, user1, user2, new Date(), null));
		return chat;
	}
}
