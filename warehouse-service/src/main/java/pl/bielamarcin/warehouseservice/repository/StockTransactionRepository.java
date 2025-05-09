package pl.bielamarcin.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.warehouseservice.model.StockTransaction;

import java.util.List;
import java.util.UUID;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, UUID> {
    List<StockTransaction> findByProductId(UUID productId);

    List<StockTransaction> findByReferenceId(UUID referenceId);
}