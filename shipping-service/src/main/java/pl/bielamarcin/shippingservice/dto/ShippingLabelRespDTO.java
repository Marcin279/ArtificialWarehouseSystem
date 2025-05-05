package pl.bielamarcin.shippingservice.dto;

import java.util.UUID;

public record ShippingLabelRespDTO(
        UUID id,
        UUID shipmentId,
        String labelUrl
) {
}
