package pl.bielamarcin.ordersservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.bielamarcin.ordersservice.dto.OrderDTO;
import pl.bielamarcin.ordersservice.model.Order;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "orderItems", target = "orderItems")
    OrderDTO toDTO(Order order);

    @Mapping(source = "orderItems", target = "orderItems")
    Order toEntity(OrderDTO orderDTO);
}