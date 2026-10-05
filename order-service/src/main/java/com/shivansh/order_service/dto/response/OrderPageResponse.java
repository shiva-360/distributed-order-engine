package com.shivansh.order_service.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
@Builder
public class OrderPageResponse {

    private List<OrderResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;




}
