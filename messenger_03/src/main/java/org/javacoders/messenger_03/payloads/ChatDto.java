package org.javacoders.messenger_03.payloads;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.javacoders.messenger_03.model.Message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatDto {
	
	private String chatId;
	private Long user1;
	private Long user2;
	private Date createdOn;
    private Set<Message> messages = new HashSet<>();
}
