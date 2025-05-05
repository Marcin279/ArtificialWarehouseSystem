package pl.bielamarcin.shippingservice.dto;

import java.util.UUID;

public record ShippingLabelReqDTO(UUID shipmentId, String labelUrl) {
}
