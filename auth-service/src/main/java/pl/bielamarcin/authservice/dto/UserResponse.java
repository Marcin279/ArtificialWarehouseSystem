package pl.bielamarcin.authservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String fistName,
        String lastName,
        String role,
        LocalDateTime createdAt
) {
}
