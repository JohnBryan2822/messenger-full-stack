package org.javacoders.messenger_03.payloads;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactDto {
	
	private String contactId;
	private Long userId;
	private Long contactUserId;
	private Date addedOn;

}
