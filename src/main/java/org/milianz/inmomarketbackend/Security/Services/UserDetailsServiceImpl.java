package org.milianz.inmomarketbackend.Security.Services;

import jakarta.transaction.Transactional;
import org.milianz.inmomarketbackend.Domain.Entities.User;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private iUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User Not Found with email: " + email);
                });

        logger.info("User found - ID: {}, Name: {}, Email: {}, Role: {}",
                user.getId(), user.getName(), user.getEmail(), user.getRole());

        return user;
    }
}