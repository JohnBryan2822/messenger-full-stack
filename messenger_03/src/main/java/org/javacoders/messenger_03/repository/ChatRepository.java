package org.javacoders.messenger_03.repository;

import java.util.List;

import org.javacoders.messenger_03.model.Chat;
import org.javacoders.messenger_03.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	
	List<Chat> findByUser1(User user1);
}
