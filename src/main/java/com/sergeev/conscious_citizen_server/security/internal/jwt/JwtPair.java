package com.sergeev.conscious_citizen_server.security.internal.jwt;

public class JwtPair {

    private final String token;
    private final String refreshToken;

    public JwtPair(final String token, final String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
