package pl.bielamarcin.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.warehouseservice.model.InventoryItem;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByProductId(UUID productId);
}