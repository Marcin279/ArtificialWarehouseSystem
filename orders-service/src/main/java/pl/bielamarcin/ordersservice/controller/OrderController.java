package pl.bielamarcin.ordersservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bielamarcin.ordersservice.dto.OrderDTO;
import pl.bielamarcin.ordersservice.exception.OrderNotFoundException;
import pl.bielamarcin.ordersservice.service.OrderGrpcService;
import pl.bielamarcin.ordersservice.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderGrpcService orderGrpcService;
    @Autowired
    public OrderController(OrderService orderService, OrderGrpcService orderGrpcService) {
        this.orderService = orderService;
        this.orderGrpcService = orderGrpcService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            return ResponseEntity.ok(orderGrpcService.createOrder(orderDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/all")
    public ResponseEntity<List<OrderDTO>> createAllOrders(@RequestBody List<OrderDTO> orderDTOs) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createAllOrders(orderDTOs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}