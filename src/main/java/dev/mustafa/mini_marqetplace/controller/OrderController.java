package dev.mustafa.mini_marqetplace.controller;

import dev.mustafa.mini_marqetplace.model.dto.BaseResponse;
import dev.mustafa.mini_marqetplace.model.dto.OrderCreateDto;
import dev.mustafa.mini_marqetplace.model.dto.OrderResponse;
import dev.mustafa.mini_marqetplace.model.dto.UserSessionData;
import dev.mustafa.mini_marqetplace.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserSessionData userSessionData,
            @RequestBody OrderCreateDto orderCreateDto
    ) {
        OrderResponse response = orderService.createOrder(userSessionData.id(), orderCreateDto);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> confirmOrder(
            @AuthenticationPrincipal UserSessionData userSessionData,
            @PathVariable Integer id
    ){
        orderService.confirmOrder(id,userSessionData.id());
        return ResponseEntity.ok(new BaseResponse<>("updated"));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> cancel(
            @AuthenticationPrincipal UserSessionData userSessionData,
            @PathVariable Integer id
    ){
        orderService.cancelOrder(id,userSessionData.id());
        return ResponseEntity.ok(new BaseResponse<>("cancelled"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserSessionData userSessionData
    ){
        OrderResponse response = orderService.getById(id, userSessionData.id());
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

}
