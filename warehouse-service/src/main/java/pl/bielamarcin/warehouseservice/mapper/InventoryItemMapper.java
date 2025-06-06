package pl.bielamarcin.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.bielamarcin.warehouseservice.dto.InventoryItemReqDTO;
import pl.bielamarcin.warehouseservice.dto.InventoryItemRespDTO;
import pl.bielamarcin.warehouseservice.model.InventoryItem;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface InventoryItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "availableQuantity", target = "availableQuantity")
    @Mapping(source = "totalQuantity", target = "totalQuantity")
    @Mapping(source = "lastUpdated", target = "lastUpdated")
    InventoryItemRespDTO toDto(InventoryItem item);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "totalQuantity", target = "totalQuantity")
    InventoryItem toEntity(InventoryItemReqDTO dto);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "totalQuantity", target = "totalQuantity")
    void updateEntityFromDto(InventoryItemReqDTO dto, @MappingTarget InventoryItem entity);
}