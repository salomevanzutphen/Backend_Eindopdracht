package nl.novi.LivingInSync.controller;

import nl.novi.LivingInSync.dto.AuthRequest;
import nl.novi.LivingInSync.dto.AuthResponse;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.security.CustomUserDetailsService;
import nl.novi.LivingInSync.security.JwtService;
import nl.novi.LivingInSync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping(value = "/authenticated")
    public ResponseEntity<Object> authenticated(Authentication authentication, Principal principal) {
        return ResponseEntity.ok().body(principal);
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) throws Exception {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException ex) {
            throw new Exception("Incorrect username or password", ex);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        final String jwt = jwtService.generateToken(userDetails);

        // Retrieve User object
        User user = userService.findUserByUsername(username);

        // Log user data for debugging
        System.out.println("User Retrieved: " + user);
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Name: " + user.getName());
        System.out.println("Authorities: " + user.getAuthorities());

        // Collect all authorities (roles) the user has
        List<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        // Create an authentication response with additional user info
        AuthResponse authResponse = new AuthResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                roles
        );

        return ResponseEntity.ok(authResponse);
    }
}
