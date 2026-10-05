package com.shivansh.order_service.service;


import com.shivansh.order_service.constant.OrderStatus;
import com.shivansh.order_service.entity.Order;
import com.shivansh.order_service.exception.OrderCancellationException;
import com.shivansh.order_service.exception.OrderNotFoundException;
import com.shivansh.order_service.repository.OrderRepository;
import com.shivansh.order_service.dto.request.OrderRequest;
import com.shivansh.order_service.dto.response.OrderPageResponse;
import com.shivansh.order_service.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest request) {

        Order order = new Order();

        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.CREATED);

        Instant now = Instant.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        return mapToResponse(order);
    }


    public OrderPageResponse getAllOrders(Pageable pageable) {

        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderResponse> orders = orderPage.getContent().stream().map(this::mapToResponse)
                .toList();

        return OrderPageResponse.builder()
                .size(orderPage.getSize())
                .page(orderPage.getNumber())
                .content(orders)
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .build();
    }

    public OrderResponse cancelOrder(Long id)  {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new OrderCancellationException("Order cannot be cancelled in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }



    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }


}
