package com.sergeev.conscious_citizen_server.security.internal.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private String rawAccessToken;

    public JwtAuthenticationToken(final String rawAccessToken) {
        super((Collection<? extends GrantedAuthority>) null);
        this.rawAccessToken = rawAccessToken;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(final UserDetails userDetails) {
        super(userDetails.getAuthorities());
        super.setAuthenticated(true);
        super.eraseCredentials();
    }

    @Override
    public Object getCredentials() {
        return rawAccessToken;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.rawAccessToken = null;
    }
}
