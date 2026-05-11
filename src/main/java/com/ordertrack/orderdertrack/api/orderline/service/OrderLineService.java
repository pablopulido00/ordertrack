package com.ordertrack.orderdertrack.api.orderline.service;




import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import com.ordertrack.orderdertrack.api.order.model.enums.OrderStatus;
import com.ordertrack.orderdertrack.api.order.repository.OrderRepository;
import com.ordertrack.orderdertrack.api.order.service.OrderService;
import com.ordertrack.orderdertrack.api.orderline.mapper.OrderLineMapper;
import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineCreateRequest;
import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineResponse;
import com.ordertrack.orderdertrack.api.orderline.model.dto.OrderLineUpdateRequest;
import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import com.ordertrack.orderdertrack.api.orderline.repository.OrderLineRepository;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.api.product.repository.ProductRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderLineService {


    private final OrderLineRepository orderLineRepository;
    private final OrderLineMapper orderLineMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;



    public OrderLineResponse addLine (Long orderId, OrderLineCreateRequest request){

        Order order = findOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.DRAFT){
            throw new ConflictException("No se pueden añadir lineas si el pedido no esta en DRAFT");
        }


        Product product = findProductOrThrow(request.productId());

        if (!product.getActive()){
            throw new ConflictException("No se puede añadir un producto desactivado");
        }

        if (orderLineRepository.existsByOrderIdAndProductId(orderId, request.productId())){
            throw new ConflictException("No se puede añadir una linea con un producto ya añadido");
        }


        OrderLine orderLine = new OrderLine(order, product, request.quantity());

        OrderLine savedOrderLine = orderLineRepository.save(orderLine);

        orderService.recalculateTotal(order);

        return orderLineMapper.toResponse(savedOrderLine);


    }


    public OrderLineResponse updateLine(Long lineId, OrderLineUpdateRequest request){

        OrderLine line = findOrderLineOrThrow(lineId);

        Order order = line.getOrder();

        if (order.getStatus() != OrderStatus.DRAFT){
            throw new ConflictException("No se pueden modificar lineas si el pedido no esta en DRAFT");
        }

        line.setQuantity(request.quantity());


        BigDecimal newTotal = line.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity()));

        line.setLineTotal(newTotal);

        OrderLine updatedLine = orderLineRepository.save(line);

        orderService.recalculateTotal(order);

        return orderLineMapper.toResponse(updatedLine);

    }


    public void deleteOrderLine (Long lineId){
        OrderLine line = findOrderLineOrThrow(lineId);

        Order order = line.getOrder();

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ConflictException("No se pueden eliminar líneas si el pedido no está en DRAFT");
        }


        orderLineRepository.delete(line);

        orderService.recalculateTotal(line.getOrder());
    }


    private Order findOrderOrThrow (Long id){

        return orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Order with id:" + id + " not found"));

    }

    private OrderLine findOrderLineOrThrow (Long id){


        return orderLineRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("OrderLine with id:" + id + " not found"));

    }


    private Product findProductOrThrow(Long id){

        return productRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Product with id = " + id + " does not exist"));

    }



}
