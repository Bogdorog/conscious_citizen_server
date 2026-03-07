package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
class UserController {

    private final UserApi api;

    @PostMapping
    public Long register(@RequestBody RegisterUserRequest request) {
        return api.registerUser(request);
    }

    @GetMapping("/{email}")
    public Object get(@PathVariable String email) {
        return api.getUser(email);
    }

    @PostMapping("/{email}")
    public Object update(@RequestBody UpdateProfileRequest request) {
        return api.updateProfile(request);
    }

    @PostMapping("/login")
    public Long login(@RequestBody LoginRequest request) {
        return api.login(request);
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
