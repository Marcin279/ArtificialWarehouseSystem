package pl.bielamarcin.shippingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentRespDTO;
import pl.bielamarcin.shippingservice.model.Shipment;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    /**
     * Konwertuje ShipmentReqDTO na nowy obiekt Shipment
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "deliveredAt", expression = "java(java.time.LocalDateTime.now().plusDays(2))")
    Shipment toEntity(ShipmentReqDTO dto);

    /**
     * Konwertuje obiekt Shipment na ShipmentRespDTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "deliveredAt", source = "deliveredAt")
    ShipmentRespDTO toDto(Shipment shipment);
}