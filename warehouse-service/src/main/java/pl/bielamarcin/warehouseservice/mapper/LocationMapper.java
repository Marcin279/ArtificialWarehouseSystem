package pl.bielamarcin.warehouseservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.bielamarcin.warehouseservice.dto.LocationDTO;
import pl.bielamarcin.warehouseservice.dto.LocationReqDTO;
import pl.bielamarcin.warehouseservice.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDTO toDto(Location location);

    @Mapping(source = "section", target = "section")
    @Mapping(source = "shelf", target = "shelf")
    @Mapping(source = "bin", target = "bin")
    @Mapping(source = "capacity", target = "capacity")
    Location toEntity(LocationReqDTO dto);
}