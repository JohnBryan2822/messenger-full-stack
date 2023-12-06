package org.javacoders.messenger_03.services;

import java.util.List;

import org.javacoders.messenger_03.payloads.MessageDto;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
	List<MessageDto> getMessagesForChat(Long senderId, Long recipientId);
	MessageDto saveNewMessage(MessageDto messageDto);
}
