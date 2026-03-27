package com.sergeev.conscious_citizen_server.user.internal.service;

import com.sergeev.conscious_citizen_server.user.internal.entity.User;
import com.sergeev.conscious_citizen_server.user.internal.entity.UserDetailsImpl;
import com.sergeev.conscious_citizen_server.user.internal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + login));
        return UserDetailsImpl.build(user);
    }
}
