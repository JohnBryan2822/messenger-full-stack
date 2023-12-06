package org.javacoders.messenger_03.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats")
public class Chat {
	
	@Id
    private String chatId;
	
	@ManyToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "id")
    private User recipient;
    
    @Column(nullable = false)
	private Date createdOn;
    
    @OneToMany(mappedBy = "chat")
    private Set<Message> messages = new HashSet<>();
}











