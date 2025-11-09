package org.example.authapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String jwt;
    private String refreshToken;
}
