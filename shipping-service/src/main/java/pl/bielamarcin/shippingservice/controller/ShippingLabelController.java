package pl.bielamarcin.shippingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.shippingservice.dto.ShippingLabelRespDTO;
import pl.bielamarcin.shippingservice.service.ShippingLabelService;

import java.util.UUID;

@RestController
@RequestMapping("/api/shipping-labels")
public class ShippingLabelController {
    private final ShippingLabelService shippingLabelService;

    public ShippingLabelController(ShippingLabelService shippingLabelService) {
        this.shippingLabelService = shippingLabelService;
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<ShippingLabelRespDTO> getByShipmentId(@PathVariable UUID shipmentId) {
        return shippingLabelService.getShippingLabelByShipmentId(shipmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/regenerate/{shipmentId}")
    public ResponseEntity<ShippingLabelRespDTO> regenerateLabel(@PathVariable UUID shipmentId) {
        return ResponseEntity.ok(shippingLabelService.createShippingLabel(shipmentId));
    }
}