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
@Table(name = "contacts")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contactId;
	private String contactName;
	
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
	private User userId;
	
	@ManyToOne
    @JoinColumn(name = "contact_user_id", referencedColumnName = "id")
	private User contactUserId;
	
	@Column(nullable = false)
	private Date addedOn;
}
