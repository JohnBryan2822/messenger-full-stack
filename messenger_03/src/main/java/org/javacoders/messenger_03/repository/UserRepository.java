package org.javacoders.messenger_03.repository;

import java.util.List;
import java.util.Optional;

import org.javacoders.messenger_03.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String username);
	
	List<User> findByUsernameOrUsernameStartsWith(String username, String usernameStartWith);
}
