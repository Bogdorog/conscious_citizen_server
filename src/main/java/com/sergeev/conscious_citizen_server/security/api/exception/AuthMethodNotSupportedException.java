package com.sergeev.conscious_citizen_server.security.api.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    public AuthMethodNotSupportedException(final String msg) {
        super(msg);
    }
}
