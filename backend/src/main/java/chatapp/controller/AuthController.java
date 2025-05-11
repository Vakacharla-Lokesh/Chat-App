package main.java.chatapp.controller;

import main.java.chatapp.model.User;
import main.java.chatapp.repository.UserRepository;
import main.java.chatapp.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Remove password from response
        savedUser.setPassword(null);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.get("username"),
                    loginRequest.get("password")
                )
            );
            
            // Generate JWT token
            String token = jwtService.generateToken(loginRequest.get("username"));
            
            // Get user details
            User user = userRepository.findByUsername(loginRequest.get("username")).orElseThrow();
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            
            // Remove password from response
            user.setPassword(null);
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        user.setPassword(null);
        
        return ResponseEntity.ok(user);
    }
}