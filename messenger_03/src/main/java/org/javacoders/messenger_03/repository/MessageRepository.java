package org.javacoders.messenger_03.repository;

import java.util.List;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findByChat(Chat chat);
}
