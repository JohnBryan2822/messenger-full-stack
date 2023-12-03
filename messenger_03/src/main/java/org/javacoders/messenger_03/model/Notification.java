package org.javacoders.messenger_03.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
	private Long id;
	private Long senderId;
	private Long recipientId;
	private String content;
	private int unreadMessages;
}
