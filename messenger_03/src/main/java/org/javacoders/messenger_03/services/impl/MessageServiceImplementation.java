package org.javacoders.messenger_03.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.Message;
import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.javacoders.messenger_03.payloads.MessageDto;
import org.javacoders.messenger_03.repository.MessageRepository;
import org.javacoders.messenger_03.repository.UnreadMessageCountRepository;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.services.ChatService;
import org.javacoders.messenger_03.services.MessageService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImplementation implements MessageService {
	
	private final UnreadMessageCountRepository unreadMessageCountRepository;
	private final MessageRepository messageRepository;
	private final ChatService chatService;
	private final UserRepository userRepository;
	
	@Override
	public List<MessageDto> getMessagesForChat(Long myId, Long chatUserId) {
		
		Chat chat = this.chatService.getChat(myId, chatUserId);
		
		List<Message> messages = this.messageRepository.findByChat(chat);
		if(messages == null) messages = new ArrayList<>();
		
		List<MessageDto> messageDtos = messages.stream().map(message -> {
			return new MessageDto(message.getMessageId(),
					message.getChat().getChatId(),
					message.getSender().getId(),
					message.getRecipient().getId(),
					message.getMessageText(),
					message.getTimestamp());
		}).collect(Collectors.toList());
		
		return messageDtos;
	}

	@Override
	public MessageDto saveNewMessage(MessageDto messageDto) {
		Chat chat = this.chatService
				.getChat(messageDto.getSenderId(), messageDto.getRecipientId());
		
		Message message = Message.builder()
				.sender(this.userRepository.findById(messageDto.getSenderId()).orElseThrow())
				.recipient(this.userRepository.findById(messageDto.getRecipientId()).orElseThrow())
				.chat(chat)
				.messageText(messageDto.getMessageText())
				.timestamp(messageDto.getTimestamp())
				.build();
		this.messageRepository.save(message);
		messageDto.setChatId(message.getChat().getChatId());
		return messageDto;
	}

	@Override
	public int updateUnreadMessageCount(Long senderId, Long recipientId) {
		Optional<UnreadMessageCount> unreadMessageCountOptional =
                unreadMessageCountRepository.findBySenderIdAndRecipientId(senderId, recipientId);

        if (unreadMessageCountOptional.isPresent()) {
            // If entry exists, increment the unread count
            UnreadMessageCount unreadMessageCount = unreadMessageCountOptional.get();
            unreadMessageCount.setUnreadCount(unreadMessageCount.getUnreadCount() + 1);
            unreadMessageCountRepository.save(unreadMessageCount);
            return unreadMessageCount.getUnreadCount();
        } else {
            // If entry doesn't exist, create a new one
            UnreadMessageCount newUnreadMessageCount = new UnreadMessageCount();
            newUnreadMessageCount.setSenderId(senderId);
            newUnreadMessageCount.setRecipientId(recipientId);
            newUnreadMessageCount.setUnreadCount(1);
            unreadMessageCountRepository.save(newUnreadMessageCount);
            return 1;
        }
	}

	@Override
	public void setUnreadMessageCountToZero(Long senderId, Long recipientId) {
		this.unreadMessageCountRepository.deleteBySenderIdAndRecipientId(senderId, recipientId);
	}

	@Override
	public UnreadMessageCount getUnreadMessageCount(Long senderId, Long recipientId) {
		UnreadMessageCount unreadMessageCount = this
				.unreadMessageCountRepository
				.findBySenderIdAndRecipientId(senderId, recipientId)
				.orElseThrow();
		return unreadMessageCount;
	}

}
