package pl.bielamarcin.warehouseservice.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pl.bielamarcin.warehouseservice.service.InventoryService;
import pl.bielamarcin.warehouseservice.service.LocationService;

@GrpcService
public class WarehouseGrpcServiceImpl extends WarehouseServiceGrpc.WarehouseServiceImplBase {

    private final InventoryService inventoryService;
    private final LocationService locationService;

    public WarehouseGrpcServiceImpl(InventoryService inventoryService, LocationService locationService) {
        this.inventoryService = inventoryService;
        this.locationService = locationService;
    }

    @Override
    public void checkInventory(InventoryRequest request, StreamObserver<InventoryResponse> responseObserver) {
//        var productId = UUID.fromString(request.getProductId());
//        var inventoryItem = inventoryService.getInventoryItem(productId);
//
//        InventoryResponse response;
//        if (inventoryItem.isPresent()) {
//            var item = inventoryItem.get();
//            response = InventoryResponse.newBuilder()
//                    .setProductId(item.getProductId().toString())
//                    .setQuantity(item.getAvailableQuantity())
//                    .setAvailable(item.getAvailableQuantity() > 0)
//                    .build();
//        } else {
//            response = InventoryResponse.newBuilder()
//                    .setProductId(request.getProductId())
//                    .setQuantity(0)
//                    .setAvailable(false)
//                    .build();
//        }
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
    }

    @Override
    public void reserveItems(ReservationRequest request, StreamObserver<ReservationResponse> responseObserver) {
//        var orderId = UUID.fromString(request.getOrderId());
//        var items = request.getItemsList().stream()
//                .map(item -> new pl.bielamarcin.warehouseservice.dto.ItemDTO(
//                        UUID.fromString(item.getProductId()),
//                        item.getQuantity()
//                ))
//                .collect(Collectors.toList());
//
//        var result = inventoryService.reserveItems(orderId, items);
//
//        var responseBuilder = ReservationResponse.newBuilder()
//                .setReservationId(result.getReservationId().toString())
//                .setSuccess(result.isSuccess())
//                .setMessage(result.getMessage());
//
//        if (!result.getUnavailableItems().isEmpty()) {
//            result.getUnavailableItems().forEach(unavailableItem -> {
//                responseBuilder.addUnavailableItems(UnavailableItem.newBuilder()
//                        .setProductId(unavailableItem.getProductId().toString())
//                        .setRequestedQuantity(unavailableItem.getRequestedQuantity())
//                        .setAvailableQuantity(unavailableItem.getAvailableQuantity())
//                        .build());
//            });
//        }
//
//        responseObserver.onNext(responseBuilder.build());
//        responseObserver.onCompleted();
    }

    @Override
    public void releaseItems(ReleaseRequest request, StreamObserver<ReleaseResponse> responseObserver) {
//        var reservationId = UUID.fromString(request.getReservationId());
//        var result = inventoryService.releaseReservation(reservationId);
//
//        var response = ReleaseResponse.newBuilder()
//                .setSuccess(result.isSuccess())
//                .setMessage(result.getMessage())
//                .build();
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
    }
}