package pl.bielamarcin.shippingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.shippingservice.dto.ShipmentRespDTO;
import pl.bielamarcin.shippingservice.dto.ShipmentReqDTO;
import pl.bielamarcin.shippingservice.model.ShipmentStatus;
import pl.bielamarcin.shippingservice.service.ShipmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentRespDTO> createShipment(@RequestBody ShipmentReqDTO request) {
        return ResponseEntity.ok(shipmentService.createShipment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentRespDTO> getShipment(@PathVariable UUID id) {
        return shipmentService.findShipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ShipmentRespDTO>> getShipmentsByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(shipmentService.getShipmentsByOrderId(orderId));
    }

    @GetMapping("/label/{id}")
    public ResponseEntity<byte[]> generateShippingLabel(@PathVariable UUID id) {
        byte[] pdfData = shipmentService.generateShippingLabel(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=shipping-label-" + id + ".pdf")
                .body(pdfData);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentRespDTO> updateShipmentStatus(
            @PathVariable UUID id,
            @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, status));
    }
}