package pl.bielamarcin.shippingservice.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.bielamarcin.shippingservice.dto.ShippingLabelReqDTO;
import pl.bielamarcin.shippingservice.dto.ShippingLabelRespDTO;
import pl.bielamarcin.shippingservice.model.ShippingLabel;

@Mapper(componentModel = "spring")
public interface ShippingLabelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shipmentId", source = "shipmentId")
    @Mapping(target = "labelUrl", source = "labelUrl")
    ShippingLabel toEntity(ShippingLabelReqDTO shippingLabelReqDTO);

    @Mapping(target = "shipmentId", source = "shipmentId")
    @Mapping(target = "labelUrl", source = "labelUrl")
    ShippingLabelRespDTO toDto(ShippingLabel shippingLabel);
}
