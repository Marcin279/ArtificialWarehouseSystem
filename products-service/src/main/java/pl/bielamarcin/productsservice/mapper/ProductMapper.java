package pl.bielamarcin.productsservice.mapper;

import org.mapstruct.Mapper;
import pl.bielamarcin.productsservice.dto.ProductDTO;
import pl.bielamarcin.productsservice.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO productDTO);
}
