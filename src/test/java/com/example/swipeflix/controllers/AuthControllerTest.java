package com.example.swipeflix.controllers;

import com.example.swipeflix.models.ERole;
import com.example.swipeflix.models.Role;
import com.example.swipeflix.models.User;
import com.example.swipeflix.payload.request.LoginRequest;
import com.example.swipeflix.payload.request.SignUpRequest;
import com.example.swipeflix.payload.response.JwtResponse;
import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.security.jwt.JwtUtils;
import com.example.swipeflix.security.services.UserDetailsImpl;
import com.example.swipeflix.services.AuthService;
import com.example.swipeflix.services.RoleService;
import com.example.swipeflix.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_ShouldReturnJwtResponse_WhenCredentialsAreCorrect() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(1L, "user@example.com", "password",Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("jwt-token");

        ResponseEntity<?> response = authService.authenticateUser(loginRequest);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwt-token", jwtResponse.getToken());
        assertEquals("user@example.com", userDetailsImpl.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(any(Authentication.class));
    }

    @Test
    void registerUser_ShouldReturnErrorMessage_WhenEmailIsAlreadyInUse() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("user@example.com");
        signUpRequest.setPassword("password");

        when(userService.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authService.registerUser(signUpRequest);

        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Error: Email is already in use!", messageResponse.getMessage());
        verify(userService, times(1)).existsByEmail(signUpRequest.getEmail());
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage_WhenUserIsRegisteredSuccessfully() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("user@example.com");
        signUpRequest.setPassword("password");

        when(userService.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        when(roleService.findByName(ERole.ROLE_USER)).thenReturn(new Role(ERole.ROLE_USER));
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encoded-password");

        ResponseEntity<?> response = authService.registerUser(signUpRequest);

        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("User registered successfully!", messageResponse.getMessage());
        verify(userService, times(1)).existsByEmail(signUpRequest.getEmail());
        verify(userService, times(1)).save(any(User.class));
    }
}