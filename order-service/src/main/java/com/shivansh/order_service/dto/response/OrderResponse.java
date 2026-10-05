package com.shivansh.order_service.dto.response;


import com.shivansh.order_service.constant.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class OrderResponse {

    private Long id;
    private Long customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;


}
