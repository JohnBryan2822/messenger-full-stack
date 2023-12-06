package org.javacoders.messenger_03.payloads;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
	private Long messageId;
	private String chatId;
	private Long senderId;
	private Long recipientId;
	private String messageText;
	private Date timestamp;
	
	public MessageDto(Long recipientId, String messageText, Date timestamp) {
		this.recipientId = recipientId;
		this.messageText = messageText;
		this.timestamp = timestamp;
	}
}
