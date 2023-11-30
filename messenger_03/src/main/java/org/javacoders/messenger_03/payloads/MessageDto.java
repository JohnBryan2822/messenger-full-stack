package org.javacoders.messenger_03.payloads;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDto {
	private Long messageId;
	private Long chat;
	private Long senderId;
	private String messageText;
	private Date timestamp;
}
