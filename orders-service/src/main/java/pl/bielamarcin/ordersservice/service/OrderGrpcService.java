package pl.bielamarcin.ordersservice.service;

import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bielamarcin.ordersservice.dto.OrderDTO;
import pl.bielamarcin.ordersservice.dto.OrderReqDTO;
import pl.bielamarcin.ordersservice.dto.ProductDTO;
import pl.bielamarcin.ordersservice.exception.ProductNotFoundException;
import pl.bielamarcin.ordersservice.exception.ServiceGrpcCommunicationException;
import pl.bielamarcin.ordersservice.mapper.OrderMapper;
import pl.bielamarcin.ordersservice.model.Order;
import pl.bielamarcin.ordersservice.model.OrderItem;
import pl.bielamarcin.ordersservice.repository.OrderItemRepository;
import pl.bielamarcin.ordersservice.repository.OrderRepository;

import java.math.BigDecimal;
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
    private static final Logger logger = LoggerFactory.getLogger(OrderGrpcService.class);

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
    public OrderDTO createOrder(OrderReqDTO orderReqDTO) {
        Order order = orderMapper.toEntity(orderReqDTO);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(orderReqDTO.status());
        order.setShippingAddress(orderReqDTO.shippingAddress());

        final Order finalOrder = order;
        List<OrderItem> orderItems = orderReqDTO.orderItems().stream().map(itemDTO -> {
            ProductDTO product;
            try {
                product = productService.getProductById(itemDTO.id());
            } catch (StatusRuntimeException e) {
                throw new ServiceGrpcCommunicationException("Błąd komunikacji gRPC: " + e.getMessage());
            } catch (ProductNotFoundException e) {
                throw new ServiceGrpcCommunicationException("Produkt nie znaleziony: " + e.getMessage());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setOrder(finalOrder);
            return orderItem;
        }).collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        OrderDTO savedOrderDTO = orderMapper.toDTO(order);

        kafkaTemplate.send("order-created-topic", savedOrderDTO);

        logger.info("Order created: {}", savedOrderDTO);
        return savedOrderDTO;
    }
}