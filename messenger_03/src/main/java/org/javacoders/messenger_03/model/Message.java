package org.javacoders.messenger_03.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

	@ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "chatId")
	private Chat chat;
	
	@ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;
	
	@Column(nullable = false, length = 500)
    private String messageText;
	
	@Column(nullable = false)
	private Date timestamp;
	
}
