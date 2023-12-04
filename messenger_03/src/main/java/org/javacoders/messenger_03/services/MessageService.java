package org.javacoders.messenger_03.services;

import java.util.List;

import org.javacoders.messenger_03.model.Message;
import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.javacoders.messenger_03.payloads.MessageDto;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
	List<MessageDto> getMessagesForChat(Long senderId, Long recipientId);
	MessageDto saveNewMessage(MessageDto messageDto);
	int updateUnreadMessageCount(Long senderId, Long recipientId);
	void setUnreadMessageCountToZero(Long senderId, Long recipientId);
	UnreadMessageCount getUnreadMessageCount(Long senderId, Long recipientId);
}
