package org.javacoders.messenger_03.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.repository.ChatRepository;
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

	@Override
	public List<UserDto> getChatsForUser(Long userId) {
		
		User sender = this.userRepository.findById(userId).orElseThrow();
		List<Chat> chats = this.chatRepository.findBySender(sender);
		List<User> users = chats.stream().map(Chat::getRecipient).collect(Collectors.toList());
		
		return users
				.stream()
				.map(user -> this.modelMapper.map(user, UserDto.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public Optional<Chat> getChat(
			Long senderId,
			Long recipientId,
			boolean createNewRoomIfNotExists)
	{
		
		User sender = this.userRepository.findById(senderId).orElseThrow();
		User recipient = this.userRepository.findById(recipientId).orElseThrow();
		return this.chatRepository
				.findBySenderAndRecipient(sender, recipient)
				.or(() -> {
					if(createNewRoomIfNotExists) {
						return Optional.of(createChat(senderId, recipientId));
					}
					return Optional.empty();
				});
	}

	private Chat createChat(Long senderId, Long recipientId) {
		
		User user1 = this.userRepository.findById(senderId).orElseThrow();
		User user2 = this.userRepository.findById(recipientId).orElseThrow();
		
		Chat chat = new Chat(null, user1, user2, new Date(), null);
		Chat savedChat = this.chatRepository.save(chat);
		
		return savedChat;
	}
}
