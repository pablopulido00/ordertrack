package com.ordertrack.orderdertrack.api.orderline.controller;


import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineCreateRequest;
import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineResponse;
import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineUpdateRequest;
import com.ordertrack.orderdertrack.api.orderline.service.OrderLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class OrderLineAdminController {

    private final OrderLineService orderLineService;

    @PostMapping("/orders/{orderId}/lines")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderLineResponse addLine(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderLineCreateRequest request
    ) {
        return orderLineService.addLine(orderId, request);
    }

    @PutMapping("/order-lines/{lineId}")
    public OrderLineResponse updateLine(
            @PathVariable Long lineId,
            @Valid @RequestBody OrderLineUpdateRequest request
    ) {
        return orderLineService.updateLine(lineId, request);
    }

    @DeleteMapping("/order-lines/{lineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLine(@PathVariable Long lineId) {
        orderLineService.deleteOrderLine(lineId);
    }
}
