package com.sergeev.conscious_citizen_server.user.internal.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordChangeService {
    public String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(bytes);
    }

    public String hashToken(String token) {
        //return DigestUtils.sha256Hex(token);
        String sha3Hex = new DigestUtils("SHA3-256").digestAsHex(token);
        return sha3Hex;
    }
}
