package org.javacoders.messenger_03.payloads;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
	private Long messageId;
	private String chatId;
	private Long senderId;
	private Long recipientId;
	private String messageText;
	private Date timestamp;
}
