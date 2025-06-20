package org.milianz.inmomarketbackend.Payload.Response;

import lombok.Data;
import java.util.UUID;

@Data
public class UserInfoResponse {
    private UUID id;
    private String username;
    private String email;
    private String profilePicture;
}
