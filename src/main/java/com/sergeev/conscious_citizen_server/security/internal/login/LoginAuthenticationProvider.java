package com.sergeev.conscious_citizen_server.security.internal.login;

import com.sergeev.conscious_citizen_server.user.internal.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder encoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public LoginAuthenticationProvider(final UserDetailsServiceImpl userDetailsService) {
        this.encoder = new BCryptPasswordEncoder();
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        String username = (String) authentication.getPrincipal();
        String password = authentication.getCredentials().toString();
        UserDetails securityUser = authenticateByUsernameAndPassword(username, password);
        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    private UserDetails authenticateByUsernameAndPassword(final String email, final String password) {
        UserDetails user = userDetailsService.loadUserByUsername(email);
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("exception.badCredentials");
        }
        return user;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
