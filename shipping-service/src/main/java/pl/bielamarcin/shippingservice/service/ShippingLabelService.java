package pl.bielamarcin.shippingservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.shippingservice.dto.ShippingLabelReqDTO;
import pl.bielamarcin.shippingservice.dto.ShippingLabelRespDTO;
import pl.bielamarcin.shippingservice.mapper.ShippingLabelMapper;
import pl.bielamarcin.shippingservice.model.ShippingLabel;
import pl.bielamarcin.shippingservice.repository.ShippingLabelRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShippingLabelService {

    private final ShippingLabelRepository shippingLabelRepository;
    private final ShippingLabelMapper shippingLabelMapper;

    public ShippingLabelService(ShippingLabelRepository shippingLabelRepository,
                                ShippingLabelMapper shippingLabelMapper) {
        this.shippingLabelRepository = shippingLabelRepository;
        this.shippingLabelMapper = shippingLabelMapper;
    }

    public ShippingLabelRespDTO createShippingLabel(UUID shipmentId) {

        String labelUrl = "https://shipping-labels-storage.example.com/" + shipmentId + ".pdf";

        ShippingLabel shippingLabel = new ShippingLabel();
        shippingLabel.setShipmentId(shipmentId);
        shippingLabel.setLabelUrl(labelUrl);

        ShippingLabel savedLabel = shippingLabelRepository.save(shippingLabel);
        return shippingLabelMapper.toDto(savedLabel);
    }

    public Optional<ShippingLabelRespDTO> getShippingLabelByShipmentId(UUID shipmentId) {
        return shippingLabelRepository.findByShipmentId(shipmentId)
                .map(shippingLabelMapper::toDto);
    }
}