package pl.bielamarcin.productsservice.mapper;

import org.mapstruct.Mapper;
import pl.bielamarcin.productsservice.dto.ProductReqDTO;
import pl.bielamarcin.productsservice.dto.ProductRespDTO;
import pl.bielamarcin.productsservice.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductRespDTO toDTO(Product product);

    Product toEntity(ProductReqDTO productReqDTO);
}
