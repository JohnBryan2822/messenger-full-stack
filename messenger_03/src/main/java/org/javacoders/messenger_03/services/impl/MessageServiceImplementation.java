package org.javacoders.messenger_03.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.Message;
import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.javacoders.messenger_03.repository.MessageRepository;
import org.javacoders.messenger_03.repository.UnreadMessageCountRepository;
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
	
	@Override
	public List<Message> getMessagesForChat(Long senderId, Long recipientId) {
		
		var chat = this.chatService.getChat(senderId, recipientId, false);
		return chat.map(this.messageRepository::findByChat).orElse(new ArrayList<>());
	}

	@Override
	public Message saveNewMessage(Message message) {
		Chat chat = this.chatService
				.getChat(message.getSender().getId(), message.getRecipient().getId(), false)
				.orElseThrow();
		message.setChat(chat);
		this.messageRepository.save(message);
		return message;
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
