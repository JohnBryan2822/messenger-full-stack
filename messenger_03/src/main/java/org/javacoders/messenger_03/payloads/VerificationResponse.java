package org.javacoders.messenger_03.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerificationResponse {
    private String message;
    private boolean success;
}