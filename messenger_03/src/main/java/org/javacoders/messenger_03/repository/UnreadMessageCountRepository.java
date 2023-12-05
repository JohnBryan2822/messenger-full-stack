package org.javacoders.messenger_03.repository;

import java.util.Optional;

import org.javacoders.messenger_03.model.UnreadMessageCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnreadMessageCountRepository extends JpaRepository<UnreadMessageCount, Long> {
	Optional<UnreadMessageCount> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
	void deleteBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
