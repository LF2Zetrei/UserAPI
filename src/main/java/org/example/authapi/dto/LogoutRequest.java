package org.example.authapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogoutRequest {
    private String jwt;
    private String refreshToken;

    public LogoutRequest(String jwt) {
        this.jwt = jwt;
    }
}
