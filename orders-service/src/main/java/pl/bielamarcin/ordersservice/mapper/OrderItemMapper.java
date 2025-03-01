package pl.bielamarcin.ordersservice.mapper;

import org.mapstruct.Mapper;
import pl.bielamarcin.ordersservice.dto.OrderItemDTO;
import pl.bielamarcin.ordersservice.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemDTO toDTO(OrderItem orderItem);

    OrderItem toEntity(OrderItemDTO orderItemDTO);
}