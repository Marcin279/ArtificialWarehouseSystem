package pl.bielamarcin.warehouseservice.dto;

import java.util.List;
import java.util.UUID;

public record ReservationRequestDTO(
        UUID orderId,
        List<ItemDTO> items
) {
}