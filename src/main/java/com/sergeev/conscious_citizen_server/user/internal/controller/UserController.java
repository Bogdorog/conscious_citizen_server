package com.sergeev.conscious_citizen_server.user.internal.controller;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.request.PasswordResetConfirmRequest;
import com.sergeev.conscious_citizen_server.user.api.dto.request.PasswordResetRequest;
import com.sergeev.conscious_citizen_server.user.api.dto.request.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
class UserController {

    private final UserApi api;

    @GetMapping("/{login}")
    public Object get(@PathVariable String login) {
        return api.getUser(login);
    }

    @GetMapping("/{login}/role")
    public String getRole(@PathVariable String login) {
        return api.getRole(login);
    }

    @PostMapping("/{login}")
    public Object update(@RequestBody UpdateProfileRequest request) {
        return api.updateProfile(request);
    }

    @PostMapping("/password/reset/request")
    public void requestReset(@RequestBody PasswordResetRequest request) {
        api.initiatePasswordReset(request);
    }

    @PostMapping("/password/reset/confirm")
    public void confirmReset(@RequestBody PasswordResetConfirmRequest request) {
        api.confirmPasswordReset(request);
    }
}
