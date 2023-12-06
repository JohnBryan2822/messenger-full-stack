package org.javacoders.messenger_03.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewMessage {
	
	private Long senderId;
	private Long recipientId;
	private String messageText;
}
