package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.LoginRequest;
import com.university.portobellohub.dto.request.RegisterRequest;
import com.university.portobellohub.dto.response.AuthResponse;
import com.university.portobellohub.dto.response.UserResponse;
import com.university.portobellohub.entity.Role;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.repository.RoleRepository;
import com.university.portobellohub.repository.UserRepository;
import com.university.portobellohub.security.JwtTokenProvider;
import com.university.portobellohub.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Controllo se email o username esistono gia'
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use");
        }

        // Recupero il ruolo di base
        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new BadRequestException("Default role not configured"));

        // Creo il nuovo utente
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(Set.of(customerRole));

        // Salvo l'utente nel database
        return UserResponse.fromEntity(userRepository.save(user), true);
    }

    public AuthResponse login(LoginRequest request) {
        // Gestione del login
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);

        // Prendo i ruoli con un ciclo for al posto degli stream
        java.util.Set<String> roles = new java.util.HashSet<>();
        for (org.springframework.security.core.GrantedAuthority authority : principal.getAuthorities()) {
            roles.add(authority.getAuthority());
        }

        return new AuthResponse(
                token,
                principal.getId(),
                principal.getUsername(),
                principal.getUser().getUsername(),
                roles
        );
    }
}
