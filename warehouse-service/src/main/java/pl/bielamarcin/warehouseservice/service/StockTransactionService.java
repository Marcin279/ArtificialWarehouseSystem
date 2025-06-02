package pl.bielamarcin.warehouseservice.service;

import org.springframework.stereotype.Service;
import pl.bielamarcin.warehouseservice.model.StockTransaction;
import pl.bielamarcin.warehouseservice.repository.StockTransactionRepository;

import java.util.List;
import java.util.UUID;

@Service
public class StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;

    public StockTransactionService(StockTransactionRepository stockTransactionRepository) {
        this.stockTransactionRepository = stockTransactionRepository;
    }

    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository.findAll();
    }

    public List<StockTransaction> getTransactionsByProductId(UUID productId) {
        return stockTransactionRepository.findByProductId(productId);
    }

    public List<StockTransaction> getTransactionsByReferenceId(UUID referenceId) {
        return stockTransactionRepository.findByReferenceId(referenceId);
    }
}