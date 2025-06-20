package org.milianz.inmomarketbackend.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String name;
    private String email;
    private String profilePicture;
    private List<String> roles;

    public JwtResponse(String accessToken, UUID id, String name, String email, String profilePicture, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.roles = roles;
    }
}