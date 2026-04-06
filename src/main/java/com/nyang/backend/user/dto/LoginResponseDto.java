package com.nyang.backend.user.dto;

import com.nyang.backend.user.entity.Role;
import com.nyang.backend.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String tokenType;
    private String email;
    private String name;
    private Role role;

    public LoginResponseDto(Users users, String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.email = users.getEmail();
        this.name = users.getName();
        this.role = users.getRole();
    }
}