package pl.bielamarcin.ordersservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bielamarcin.ordersservice.dto.OrderDTO;
import pl.bielamarcin.ordersservice.dto.OrderItemDTO;
import pl.bielamarcin.ordersservice.dto.ProductDTO;
import pl.bielamarcin.ordersservice.exception.OrderNotFoundException;
import pl.bielamarcin.ordersservice.mapper.OrderMapper;
import pl.bielamarcin.ordersservice.mapper.OrderItemMapper;
import pl.bielamarcin.ordersservice.model.Order;
import pl.bielamarcin.ordersservice.model.OrderItem;
import pl.bielamarcin.ordersservice.repository.OrderRepository;
import pl.bielamarcin.ordersservice.repository.OrderItemRepository;
import pl.bielamarcin.ordersservice.service.ProductServiceClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private ProductServiceClient productServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return orderMapper.toDTO(order);
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(orderDTO.getStatus());
        order.setTotalPrice(orderDTO.getTotalPrice());

        final Order finalOrder = order;
        List<OrderItem> orderItems = orderDTO.getOrderItems().stream().map(itemDTO -> {
            ProductDTO product = productServiceClient.getProductById(itemDTO.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(finalOrder);
            return orderItem;
        }).toList();

        order = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        return orderMapper.toDTO(order);
    }

    public OrderDTO updateOrder(UUID id, OrderDTO updatedOrderDTO) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(updatedOrderDTO.getStatus());
        order.setTotalPrice(updatedOrderDTO.getTotalPrice());
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        return orderMapper.toDTO(order);
    }

    public void deleteOrder(UUID id) throws OrderNotFoundException {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    public List<OrderDTO> createAllOrders(List<OrderDTO> orderDTOs) {
        List<Order> orders = orderDTOs.stream()
                .map(orderMapper::toEntity)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        orders.forEach(order -> {
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
        });
        orders = orderRepository.saveAll(orders);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }
}