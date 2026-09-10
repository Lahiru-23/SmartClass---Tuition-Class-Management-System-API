package lk.SmartClass.service.impl;


import lk.SmartClass.dto.request.LoginRequest;
import lk.SmartClass.dto.request.RegisterRequest;
import lk.SmartClass.dto.response.AuthResponse;
import lk.SmartClass.entity.Role;
import lk.SmartClass.entity.User;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.repository.RoleRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.repository.TeacherRepository;
import lk.SmartClass.repository.UserRepository;
import lk.SmartClass.security.JwtProperties;
import lk.SmartClass.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        String requestedRole = request.getRole().toUpperCase();
        if (requestedRole.equals("ADMIN")) {
            throw new BadRequestException("Cannot self-register as ADMIN");
        }

        Role role = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> new BadRequestException("Invalid role: " + requestedRole));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
        log.info("New user registered: '{}' with role '{}'", user.getUsername(), requestedRole);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return buildAuthResponse(userDetails);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        log.info("User '{}' logged in successfully", request.getUsername());

        return buildAuthResponse(userDetails);
    }

    private AuthResponse buildAuthResponse(UserDetails userDetails) {
        String token = jwtService.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(Object::toString).toList();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found: " + userDetails.getUsername()));

        Long studentId = studentRepository.findByUserId(user.getId()).map(s -> s.getId()).orElse(null);
        Long teacherId = teacherRepository.findByUserId(user.getId()).map(t -> t.getId()).orElse(null);

        return AuthResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .roles(roles)
                .expiresInMs(jwtProperties.getExpirationMs())
                .userId(user.getId())
                .studentId(studentId)
                .teacherId(teacherId)
                .build();
    }
}

