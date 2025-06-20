package org.milianz.inmomarketbackend.Security.Services;


import org.milianz.inmomarketbackend.Domain.Entities.User;
import org.milianz.inmomarketbackend.Domain.Repositories.iUserRepository;
import org.milianz.inmomarketbackend.Payload.Request.LoginRequest;
import org.milianz.inmomarketbackend.Payload.Request.SingupRequest;
import org.milianz.inmomarketbackend.Payload.Response.JwtResponse;
import org.milianz.inmomarketbackend.Payload.Response.MessageResponse;
import org.milianz.inmomarketbackend.Security.JTW.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private iUserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        try {
            logger.info("Attempting to authenticate user with email: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            logger.info("User authenticated successfully. Authorities: {}", user.getAuthorities());

            String jwt = jwtUtils.generateJwtToken(user);

            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getProfilePicture(),
                    user.getAuthorities().stream()
                            .map(item -> item.getAuthority())
                            .toList()
            );

            return ResponseEntity.ok(jwtResponse);

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}, Error: {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<?> registerUser(SingupRequest signUpRequest) {
        try {
            logger.info("Attempting to register user with email: {}", signUpRequest.getEmail());

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                logger.warn("Registration failed - Email already exists: {}", signUpRequest.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
            }

            String userRole = determineUserRole(signUpRequest.getRole());
            logger.info("Assigning role '{}' to user: {}", userRole, signUpRequest.getEmail());

            User user = User.builder()
                    .name(signUpRequest.getName())
                    .email(signUpRequest.getEmail())
                    .phoneNumber(signUpRequest.getPhoneNumber())
                    .password(encoder.encode(signUpRequest.getPassword()))
                    .role(userRole)
                    .build();

            logger.info("User object before saving - Name: {}, Email: {}, Role: {}",
                    user.getName(), user.getEmail(), user.getRole());

            User savedUser = userRepository.save(user);

            logger.info("User saved successfully - ID: {}, Name: {}, Email: {}, Role: {}",
                    savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

        } catch (Exception e) {
            logger.error("Registration failed for user: {}, Error: {}", signUpRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Registration failed - " + e.getMessage()));
        }
    }

    private String determineUserRole(String requestedRole) {
        if (!StringUtils.hasText(requestedRole)) {
            return "USER"; // Rol por defecto
        }

        String normalizedRole = requestedRole.trim().toUpperCase();

        if (normalizedRole.equals("ADMIN") || normalizedRole.equals("USER") || normalizedRole.equals("MODERATOR")) {
            return normalizedRole;
        }

        logger.warn("Invalid role '{}' provided, defaulting to USER", requestedRole);
        return "USER";
    }

    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }
}