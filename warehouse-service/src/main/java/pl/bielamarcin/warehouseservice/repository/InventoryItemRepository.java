package pl.bielamarcin.warehouseservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import pl.bielamarcin.warehouseservice.model.InventoryItem;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    // Added to prevent deadlocks
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findByProductId(UUID productId);
}