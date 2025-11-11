package org.example.authapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTResponse {
    private String accessToken;
    private String username;
    private String refreshToken;

    private String tokenType = "Bearer";

    public JWTResponse(String accessToken, String username, String refreshToken) {
        this.accessToken = accessToken;
        this.username = username;
        this.refreshToken = refreshToken;
    }
}
