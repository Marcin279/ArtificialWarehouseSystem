package pl.bielamarcin.authservice.dto;

public record LoginRequest(
        String login,
        String password
) {
}
