package com.example.swipeflix.controllers;

import com.example.swipeflix.models.ERole;
import com.example.swipeflix.models.Role;
import com.example.swipeflix.models.User;
import com.example.swipeflix.models.UserPreferences;
import com.example.swipeflix.payload.request.LoginRequest;
import com.example.swipeflix.payload.request.SignUpRequest;
import com.example.swipeflix.payload.response.JwtResponse;
import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.security.jwt.JwtUtils;
import com.example.swipeflix.security.services.UserDetailsImpl;
import com.example.swipeflix.services.RoleService;
import com.example.swipeflix.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("authentication: " + authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        System.out.println("jwt" + jwt);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Setting cookie attributes
        ResponseCookie jwtCookie = ResponseCookie.from("token", jwt)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .httpOnly(false)
                .secure(false)
                .domain("localhost")
                .build();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok().headers(responseHeaders).body(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                roles));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));


        Set<Role> roles = new HashSet<>();

        Role userRole = roleService.findByName(ERole.ROLE_USER);

        roles.add(userRole);

        user.setRoles(roles);

        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUser(user);
        user.setUserPreferences(userPreferences);

        userService.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

}
