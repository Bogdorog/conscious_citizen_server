package com.sergeev.conscious_citizen_server.security.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class SigninDTO {

    @NotBlank
    @Schema(example = "username", description = "User name")
    private String username;

    @NotBlank
    @Schema(example = "password", description = "User password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
