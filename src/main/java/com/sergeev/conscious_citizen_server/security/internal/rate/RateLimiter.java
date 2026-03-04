package com.sergeev.conscious_citizen_server.security.internal.rate;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
class RateLimiter {

    private final Map<String, List<LocalDateTime>> attempts = new ConcurrentHashMap<>();

    public void check(String key, int maxAttempts, Duration window) {

        LocalDateTime now = LocalDateTime.now();

        attempts.computeIfAbsent(key, k -> new ArrayList<>());

        List<LocalDateTime> timestamps = attempts.get(key);

        timestamps.removeIf(time -> time.isBefore(now.minus(window)));

        if (timestamps.size() >= maxAttempts) {
            throw new IllegalStateException("Too many attempts");
        }

        timestamps.add(now);
    }
}
