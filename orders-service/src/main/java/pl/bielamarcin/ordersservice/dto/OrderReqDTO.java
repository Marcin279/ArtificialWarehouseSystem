package pl.bielamarcin.ordersservice.dto;

import java.util.List;

public record OrderReqDTO(
        String status,
        String shippingAddress,
        List<OrderItemDTO> orderItems
) {
}
