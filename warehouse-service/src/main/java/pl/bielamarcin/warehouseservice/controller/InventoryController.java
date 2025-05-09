package pl.bielamarcin.warehouseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.warehouseservice.dto.InventoryItemReqDTO;
import pl.bielamarcin.warehouseservice.dto.InventoryItemRespDTO;
import pl.bielamarcin.warehouseservice.dto.ItemDTO;
import pl.bielamarcin.warehouseservice.model.InventoryItem;
import pl.bielamarcin.warehouseservice.service.InventoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemRespDTO>> getAllInventoryItems() {
        return ResponseEntity.ok(inventoryService.getAllInventoryItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable UUID id) {
        return inventoryService.getInventoryItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryItem> getInventoryItemByProductId(@PathVariable UUID productId) {
        return inventoryService.getInventoryItemByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryItemRespDTO> createInventoryItem(@RequestBody InventoryItemReqDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventoryItem(dto));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<InventoryItemRespDTO> updateInventoryItem(
//            @PathVariable UUID id,
//            @RequestBody InventoryItemReqDTO dto) {
//        return ResponseEntity.ok(inventoryService.updateInventoryItem(id, dto));
//    }

    @PostMapping("/{id}/add-stock")
    public ResponseEntity<InventoryItemRespDTO> addStock(
            @PathVariable UUID id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.addStock(id, quantity));
    }

//    @PostMapping("/{id}/remove-stock")
//    public ResponseEntity<InventoryItemRespDTO> removeStock(
//            @PathVariable UUID id,
//            @RequestParam int quantity) {
//        return ResponseEntity.ok(inventoryService.removeStock(id, quantity));
//    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveItems(
            @RequestParam UUID orderId,
            @RequestBody List<ItemDTO> items) {
        return ResponseEntity.ok(inventoryService.reserveItems(orderId, items));
    }

    @PostMapping("/release")
    public ResponseEntity<?> releaseReservation(@RequestParam UUID reservationId) {
        return ResponseEntity.ok(inventoryService.releaseReservation(reservationId));
    }
}