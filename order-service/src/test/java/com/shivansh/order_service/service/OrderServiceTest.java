package com.shivansh.order_service.service;


import com.shivansh.order_service.dto.request.OrderRequest;
import com.shivansh.order_service.dto.response.OrderPageResponse;
import com.shivansh.order_service.dto.response.OrderResponse;
import com.shivansh.order_service.entity.Order;
import com.shivansh.order_service.constant.OrderStatus;
import com.shivansh.order_service.exception.OrderCancellationException;
import com.shivansh.order_service.exception.OrderNotFoundException;
import com.shivansh.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {

        order = new Order();

        order.setId(1L);
        order.setCustomerId(101L);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(new BigDecimal("1499.99"));
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {

        OrderRequest request = new OrderRequest();
        request.setCustomerId(102L);
        request.setTotalAmount(new BigDecimal("1499.99"));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(101L, response.getCustomerId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals(new BigDecimal("1499.99"), response.getTotalAmount());

        verify(orderRepository, times(1))
                .save(any(Order.class));
    }

    @Test
    void getOrderById_shouldReturnOrderSuccessfully() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponse response =
                orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(101L, response.getCustomerId());
        assertEquals(OrderStatus.CREATED, response.getStatus());

        verify(orderRepository, times(1))
                .findById(1L);
    }

    @Test
    void getOrderById_shouldThrowExceptionWhenOrderNotFound() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(999L)
        );

        verify(orderRepository, times(1))
                .findById(999L);
    }

    @Test
    void cancelOrder_shouldCancelOrderSuccessfully() {

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderResponse response =
                orderService.cancelOrder(1L);

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, response.getStatus());

        verify(orderRepository).findById(1L);

        verify(orderRepository)
                .save(order);
    }

    @Test
    void cancelOrder_shouldThrowExceptionWhenOrderCannotBeCancelled() {

        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(OrderCancellationException.class, () -> orderService.cancelOrder(1L));

        verify(orderRepository).findById(1L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void getAllOrders_shouldReturnPaginatedOrders() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        OrderPageResponse response = orderService.getAllOrders(pageable);
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(10,response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        verify(orderRepository).findAll(pageable);
    }
}
