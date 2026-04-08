package com.sergeev.conscious_citizen_server.security.internal.jwt;

import com.sergeev.conscious_citizen_server.security.api.exception.InvalidRefreshTokenException;
import com.sergeev.conscious_citizen_server.security.internal.jwt.entity.RefreshToken;
import com.sergeev.conscious_citizen_server.user.api.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RefreshJwtAuthenticationToken authToken = (RefreshJwtAuthenticationToken) authentication;
        String rawToken = authToken.getRefreshToken();

        // Валидируем токен и получаем запись из БД
        RefreshToken refreshToken;
        try {
            refreshToken = jwtTokenProvider.validateRefreshToken(rawToken);
        } catch (InvalidRefreshTokenException e) {
            log.warn("Invalid refresh token attempt: {}", e.getMessage());
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        // Загружаем владельца по userId из записи токена
        // Если пользователь удалён или заблокирован — аутентификация упадёт здесь
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserById(refreshToken.getUserId());
        } catch (Exception e) {
            log.warn("User not found for refresh token, userId: {}", refreshToken.getUserId());
            throw new BadCredentialsException("Token owner not found");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        return new RefreshJwtAuthenticationToken(userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshJwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
