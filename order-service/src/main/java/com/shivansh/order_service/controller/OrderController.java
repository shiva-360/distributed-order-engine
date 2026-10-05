package com.shivansh.order_service.controller;


import com.shivansh.order_service.dto.request.OrderRequest;
import com.shivansh.order_service.dto.response.OrderPageResponse;
import com.shivansh.order_service.dto.response.OrderResponse;
import com.shivansh.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String  healthCheck() {
        return"success";
    }

    @GetMapping
    public OrderPageResponse getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}
