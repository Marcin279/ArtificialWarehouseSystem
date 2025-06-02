package pl.bielamarcin.warehouseservice.dto;

public record LocationReqDTO(
        String section,
        String shelf,
        String bin,
        Integer capacity
) {
}
