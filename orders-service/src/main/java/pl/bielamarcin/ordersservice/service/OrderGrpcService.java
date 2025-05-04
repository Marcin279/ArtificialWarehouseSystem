package pl.bielamarcin.ordersservice.service;

import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bielamarcin.ordersservice.dto.OrderDTO;
import pl.bielamarcin.ordersservice.dto.ProductDTO;
import pl.bielamarcin.ordersservice.exception.ProductNotFoundException;
import pl.bielamarcin.ordersservice.exception.ServiceGrpcCommunicationException;
import pl.bielamarcin.ordersservice.mapper.OrderMapper;
import pl.bielamarcin.ordersservice.model.Order;
import pl.bielamarcin.ordersservice.model.OrderItem;
import pl.bielamarcin.ordersservice.repository.OrderItemRepository;
import pl.bielamarcin.ordersservice.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderGrpcService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductGrpcClientService productService;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderGrpcService(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductGrpcClientService productService,
                            OrderMapper orderMapper,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.orderMapper = orderMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(orderDTO.getStatus());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setShippingAddress(orderDTO.getShippingAddress());

        final Order finalOrder = order;
        List<OrderItem> orderItems = orderDTO.getOrderItems().stream().map(itemDTO -> {
            ProductDTO product;
            try {
                product = productService.getProductById(itemDTO.getId());
                System.out.println("Pobrany produkt: " + product);
            } catch (StatusRuntimeException e) {
                throw new ServiceGrpcCommunicationException("Błąd komunikacji gRPC: " + e.getMessage());
            } catch (ProductNotFoundException e) {
                throw new ServiceGrpcCommunicationException("Produkt nie znaleziony: " + e.getMessage());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setOrder(finalOrder);
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        OrderDTO savedOrderDTO = orderMapper.toDTO(order);

        kafkaTemplate.send("order-created-topic", savedOrderDTO);

        return savedOrderDTO;
    }
}