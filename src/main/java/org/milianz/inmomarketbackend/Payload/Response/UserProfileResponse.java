package org.milianz.inmomarketbackend.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}