package pl.bielamarcin.warehouseservice.dto;

import java.util.UUID;

public record LocationDTO(
        UUID id,
        String section,
        String shelf,
        String bin,
        Integer capacity
) {
}