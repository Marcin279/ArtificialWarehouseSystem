package pl.bielamarcin.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.bielamarcin.authservice.config.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // 🔐 tu normalnie sprawdzasz w bazie
        if ("admin".equals(username) && "password".equals(password)) {
            return jwtUtil.generateToken(username, "ADMIN");
        }
        throw new RuntimeException("Invalid credentials");
    }
}
