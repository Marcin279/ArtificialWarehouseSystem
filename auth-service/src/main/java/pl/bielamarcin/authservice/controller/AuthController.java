package pl.bielamarcin.authservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.authservice.dto.AuthResponse;
import pl.bielamarcin.authservice.dto.LoginRequest;
import pl.bielamarcin.authservice.dto.RegisterRequest;
import pl.bielamarcin.authservice.dto.UserResponse;
import pl.bielamarcin.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        UserResponse userResponse = authService.getCurrentUser(token);
        return ResponseEntity.ok(userResponse);

    }
}
