package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import com.sergeev.conscious_citizen_server.user.api.dto.request.LoginRequest;
import com.sergeev.conscious_citizen_server.user.api.dto.request.PasswordResetConfirmRequest;
import com.sergeev.conscious_citizen_server.user.api.dto.request.PasswordResetRequest;
import com.sergeev.conscious_citizen_server.user.api.dto.request.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserApi api;

    @PostMapping
    public Long register(@RequestBody RegisterUserRequest request) {
        return api.registerUser(
                request.fullName(),
                request.email(),
                request.phone(),
                request.password()
        );
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id) {
        return api.getUser(id);
    }

    @PostMapping("/login")
    public Long login(@RequestBody LoginRequest r) {
        return api.login(r.emailOrPhone(), r.password());
    }

    @PostMapping("/password/reset/request")
    public void requestReset(@RequestBody PasswordResetRequest r) {
        api.initiatePasswordReset(r.emailOrPhone());
    }

    @PostMapping("/password/reset/confirm")
    public void confirmReset(@RequestBody PasswordResetConfirmRequest r) {
        api.confirmPasswordReset(r.token(), r.newPassword());
    }
}
