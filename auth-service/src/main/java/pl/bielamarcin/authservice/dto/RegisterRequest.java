package pl.bielamarcin.authservice.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName
) {
}
