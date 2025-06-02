package pl.bielamarcin.shippingservice.dto;

import java.util.UUID;

public record ShipmentReqDTO(
        UUID orderId,
        String shippingAddress
) {
}
