package com.sergeev.conscious_citizen_server.security.internal.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredTokenException extends AuthenticationException {

    public ExpiredTokenException(final String token, final String msg, final Throwable t) {
        super(msg, t);
    }
}
