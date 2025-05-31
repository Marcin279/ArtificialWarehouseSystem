package pl.bielamarcin.warehouseservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pl.bielamarcin.warehouseservice.dto.*;
import pl.bielamarcin.warehouseservice.event.WarehouseEventProducer;
import pl.bielamarcin.warehouseservice.grpc.ProductGrpcService;
import pl.bielamarcin.warehouseservice.mapper.InventoryItemMapper;
import pl.bielamarcin.warehouseservice.model.InventoryItem;
import pl.bielamarcin.warehouseservice.model.StockTransaction;
import pl.bielamarcin.warehouseservice.model.TransactionType;
import pl.bielamarcin.warehouseservice.repository.InventoryItemRepository;
import pl.bielamarcin.warehouseservice.repository.StockTransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryItemRepository inventoryItemRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final InventoryItemMapper inventoryItemMapper;
    private final ProductGrpcService productGrpcService;
    private final LocationService locationService;
    private final WarehouseEventProducer eventProducer;

    public InventoryService(InventoryItemRepository inventoryItemRepository,
                            StockTransactionRepository stockTransactionRepository,
                            InventoryItemMapper inventoryItemMapper,
                            ProductGrpcService productGrpcService,
                            LocationService locationService,
                            WarehouseEventProducer eventProducer) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.inventoryItemMapper = inventoryItemMapper;
        this.productGrpcService = productGrpcService;
        this.locationService = locationService;
        this.eventProducer = eventProducer;
    }

    /**
     * Dodaje nowy produkt do magazynu
     */
    @Transactional
    public InventoryItemRespDTO createInventoryItem(InventoryItemReqDTO dto) {
        // Sprawdzamy czy produkt istnieje w serwisie produktów
        ProductDTO productDto = productGrpcService.getProductById(dto.productId());

        // Sprawdzamy czy produkt już istnieje w magazynie
        Optional<InventoryItem> existingItem = inventoryItemRepository.findByProductId(dto.productId());

        if (existingItem.isPresent()) {
            // Jeśli produkt już istnieje, dodajemy do stanu
            return addStock(existingItem.get().getId(), dto.totalQuantity());
        }

        // Tworzymy nowy wpis w magazynie
        InventoryItem item = new InventoryItem();
        item.setId(UUID.randomUUID());
        item.setProductId(dto.productId());
        item.setAvailableQuantity(dto.totalQuantity());
        item.setTotalQuantity(dto.totalQuantity());
        item.setReservedQuantity(0);

        InventoryItem savedItem = inventoryItemRepository.save(item);

        // Rejestrujemy transakcję dodania do stanu
        registerStockTransaction(dto.productId(), dto.totalQuantity(), TransactionType.STOCK_ADDITION, null);

        // Publikujemy zdarzenie o aktualizacji stanu
        eventProducer.sendStockUpdated(dto.productId(), dto.totalQuantity());

//        // Jeśli podano lokalizację, przypisujemy produkt do lokalizacji
//        if (dto.section() != null && dto.shelf() != null && dto.bin() != null) {
//        locationService.createLocation(savedItem.getId(), dto.section(), dto.shelf(), dto.bin());
//        }

        return inventoryItemMapper.toDto(savedItem);
    }

    /**
     * Dodaje określoną ilość do stanu magazynowego istniejącego produktu
     */
    @Transactional
    public InventoryItemRespDTO addStock(UUID inventoryItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Ilość musi być większa od zera");
        }

        InventoryItem item = inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przedmiotu o ID: " + inventoryItemId));

        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        InventoryItem updatedItem = inventoryItemRepository.save(item);

        // Rejestrujemy transakcję
        registerStockTransaction(item.getProductId(), quantity, TransactionType.STOCK_ADDITION, null);

        // Publikujemy zdarzenie o aktualizacji stanu
        eventProducer.sendStockUpdated(item.getProductId(), item.getAvailableQuantity());

        logger.info("Dodano {} sztuk produktu {} do magazynu", quantity, item.getProductId());

        return inventoryItemMapper.toDto(updatedItem);
    }

    private void registerStockTransaction(UUID productId, int quantity, TransactionType type, UUID referenceId) {
        StockTransaction transaction = new StockTransaction();
        transaction.setId(UUID.randomUUID());
        transaction.setProductId(productId);
        transaction.setQuantity(quantity);
        transaction.setType(type);
        transaction.setReferenceId(referenceId);

        stockTransactionRepository.save(transaction);
    }

    public List<InventoryItemRespDTO> getAllInventoryItems() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        System.out.println("items = " + items);
        return items.stream()
                .map(inventoryItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<InventoryItem> getInventoryItemByProductId(UUID productId) {
        return inventoryItemRepository.findByProductId(productId);
    }

    public Optional<InventoryItem> getInventoryItemById(UUID id) {
        return inventoryItemRepository.findById(id);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ReservationResult reserveItems(UUID orderId, List<ItemDTO> items) {
        ReservationResult result = new ReservationResult();
        result.setReservationId(UUID.randomUUID());

        // Sprawdzamy dostępność wszystkich produktów najpierw
        List<UnavailableItemDTO> unavailableItems = new ArrayList<>();
        for (ItemDTO item : items) {
            Optional<InventoryItem> inventoryItemOpt = inventoryItemRepository.findByProductId(item.productId());

            if (inventoryItemOpt.isEmpty()) {
                unavailableItems.add(new UnavailableItemDTO(item.productId(), item.quantity(), 0));
                continue;
            }

            InventoryItem inventoryItem = inventoryItemOpt.get();
            if (inventoryItem.getAvailableQuantity() < item.quantity()) {
                unavailableItems.add(new UnavailableItemDTO(
                        item.productId(),
                        item.quantity(),
                        inventoryItem.getAvailableQuantity()
                ));
            }
        }

        // Jeśli są niedostępne produkty, zwracamy błąd
        if (!unavailableItems.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("Niektóre produkty są niedostępne w wymaganej ilości");
            result.setUnavailableItems(unavailableItems);
            return result;
        }

        // Dokonujemy rezerwacji
        ReservationInfo reservationInfo = new ReservationInfo(orderId);

        for (ItemDTO item : items) {
            InventoryItem inventoryItem = inventoryItemRepository.findByProductId(item.productId()).get();

            // Aktualizujemy stan magazynowy
            inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - item.quantity());
            inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() + item.quantity());
            inventoryItemRepository.save(inventoryItem);

            // Dodajemy informację o zarezerwowanym produkcie
            reservationInfo.getReservedItems().add(new ReservedItem(item.productId(), item.quantity()));

            // Rejestrujemy transakcję
            StockTransaction transaction = new StockTransaction();
            transaction.setId(UUID.randomUUID());
            transaction.setProductId(item.productId());
            transaction.setQuantity(item.quantity());
            transaction.setType(TransactionType.RESERVATION);
            transaction.setReferenceId(result.getReservationId());
            stockTransactionRepository.save(transaction);

            // Publikujemy zdarzenie
            eventProducer.sendProductReserved(item.productId(), orderId, item.quantity());
        }

        // Zapisujemy rezerwację
//        reservations.put(result.getReservationId(), reservationInfo);

        result.setSuccess(true);
        result.setMessage("Produkty zostały zarezerwowane");

        logger.info("Zarezerwowano produkty dla zamówienia {}, ID rezerwacji: {}",
                orderId, result.getReservationId());

        return result;
    }

    @Transactional
    public ReleaseResult releaseReservation(UUID reservationId) {
        ReleaseResult result = new ReleaseResult();

//        if (!reservations.containsKey(reservationId)) {
//            result.setSuccess(false);
//            result.setMessage("Nie znaleziono rezerwacji o ID: " + reservationId);
//            return result;
//        }
//
//        ReservationInfo reservationInfo = reservations.get(reservationId);

//        for (ReservedItem item : reservationInfo.getReservedItems()) {
//            Optional<InventoryItem> inventoryItemOpt = inventoryItemRepository.findByProductId(item.productId());
//
//            if (inventoryItemOpt.isPresent()) {
//                InventoryItem inventoryItem = inventoryItemOpt.get();
//
//                inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() + item.quantity());
//                inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() - item.quantity());
//                inventoryItemRepository.save(inventoryItem);
//
//                StockTransaction transaction = new StockTransaction();
//                transaction.setId(UUID.randomUUID());
//                transaction.setProductId(item.productId());
//                transaction.setQuantity(item.quantity());
//                transaction.setType(TransactionType.RESERVATION_RELEASE);
//                transaction.setReferenceId(reservationId);
//                stockTransactionRepository.save(transaction);
//
////                eventProducer.sendProductReleased(item.productId(), reservationInfo.getOrderId(), item.quantity());
//            }
//        }

//        reservations.remove(reservationId);

        result.setSuccess(true);
        result.setMessage("Rezerwacja została zwolniona");

        return result;
    }

    @Transactional
    public boolean confirmStockRemoval(UUID reservationId) {
//        if (!reservations.containsKey(reservationId)) {
//            logger.error("Nie znaleziono rezerwacji o ID: {}", reservationId);
//            return false;
//        }

//        ReservationInfo reservationInfo = reservations.get(reservationId);
//
//        for (ReservedItem item : reservationInfo.getReservedItems()) {
//            Optional<InventoryItem> inventoryItemOpt = inventoryItemRepository.findByProductId(item.productId());
//
//            if (inventoryItemOpt.isPresent()) {
//                InventoryItem inventoryItem = inventoryItemOpt.get();
//
//                // Zmniejszamy całkowitą ilość i ilość zarezerwowaną
//                inventoryItem.setTotalQuantity(inventoryItem.getTotalQuantity() - item.quantity());
//                inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() - item.quantity());
//                inventoryItemRepository.save(inventoryItem);
//
//                // Rejestrujemy transakcję
//                StockTransaction transaction = new StockTransaction();
//                transaction.setId(UUID.randomUUID());
//                transaction.setProductId(item.productId());
//                transaction.setQuantity(item.quantity());
//                transaction.setType(TransactionType.STOCK_REMOVAL);
//                transaction.setReferenceId(reservationId);
//                stockTransactionRepository.save(transaction);
//
//                // Publikujemy zdarzenie
//                eventProducer.sendStockUpdated(item.productId(), inventoryItem.getTotalQuantity());
//            }
//        }
//
//        reservations.remove(reservationId);
        return true;
    }

    // Klasy pomocnicze

    public static class ReservationInfo {
        private final UUID orderId;
        private final List<ReservedItem> reservedItems = new ArrayList<>();

        public ReservationInfo(UUID orderId) {
            this.orderId = orderId;
        }

        public UUID getOrderId() {
            return orderId;
        }

        public List<ReservedItem> getReservedItems() {
            return reservedItems;
        }
    }

    public record ReservedItem(UUID productId, int quantity) {
    }

    public static class ReservationResult {
        private UUID reservationId;
        private boolean success;
        private String message;
        private List<UnavailableItemDTO> unavailableItems = new ArrayList<>();

        public UUID getReservationId() {
            return reservationId;
        }

        public void setReservationId(UUID reservationId) {
            this.reservationId = reservationId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<UnavailableItemDTO> getUnavailableItems() {
            return unavailableItems;
        }

        public void setUnavailableItems(List<UnavailableItemDTO> unavailableItems) {
            this.unavailableItems = unavailableItems;
        }
    }

    public static class ReleaseResult {
        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}