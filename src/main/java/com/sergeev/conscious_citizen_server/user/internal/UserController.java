package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserApi api;

    @PostMapping
    public Long register(@RequestBody CreateUserRequest request) {
        return api.registerUser(
                request.fullName(),
                request.email(),
                request.phone()
        );
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id) {
        return api.getUser(id);
    }
}

record CreateUserRequest(String fullName, String email, String phone) {}
