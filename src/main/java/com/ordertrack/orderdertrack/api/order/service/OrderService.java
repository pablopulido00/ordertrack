package com.ordertrack.orderdertrack.api.order.service;

import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.customer.repository.CustomerRepository;
import com.ordertrack.orderdertrack.api.order.mapper.OrderMapper;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderAdminResponse;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderCreateRequest;
import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import com.ordertrack.orderdertrack.api.order.model.enums.OrderStatus;
import com.ordertrack.orderdertrack.api.order.repository.OrderRepository;
import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import com.ordertrack.orderdertrack.api.orderline.repository.OrderLineRepository;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {


    private final OrderRepository orderRepository;
    private final CustomerRepository customerRespository;
    private final OrderLineRepository orderLineRepository;
    private final OrderMapper orderMapper;

    public OrderAdminResponse createOrder (OrderCreateRequest request){

        Customer customer = findCustomerOrThrow(request.customerId());

        Order order = new Order(customer);

        Order orderSaved = orderRepository.save(order);

        return orderMapper.toAdminResponse(orderSaved);

    }

    @Transactional(readOnly = true)
    public OrderAdminResponse getOrderById(Long id) {

        Order order = findOrderOrThrow(id);

        return orderMapper.toAdminResponse(order);
    }


    public void recalculateTotal(Order order) {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderLine line : order.getOrderLines()) {
            total = total.add(line.getLineTotal());
        }

        order.setTotal(total);
    }


    public OrderAdminResponse confirmOrder (Long id){

        Order order = findOrderOrThrow(id);

        if (order.getStatus() != OrderStatus.DRAFT){
            throw new ConflictException("No se puedo confirmar un pedido que no se encuentra en DRAFT");
        }

        if (order.getOrderLines() == null || order.getOrderLines().isEmpty()){
            throw new ConflictException("No se puede confirmar un pedido sin lineas de pedido");
        }

        for (OrderLine line : order.getOrderLines()){
            if (line.getQuantity() > line.getProduct().getStock()){
                throw new ConflictException("No se puede confirmar un pedido si no hay stock suficiente");
            }
        }

        recalculateTotal(order);

        for (OrderLine line : order.getOrderLines()){

            Product product = line.getProduct();

            int newStock = product.getStock() - line.getQuantity();

            product.setStock(newStock);
        }


        order.setStatus(OrderStatus.CONFIRMED);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toAdminResponse(savedOrder);
    }


    public OrderAdminResponse cancelOrder (Long id){

        Order order = findOrderOrThrow(id);


        if (order.getStatus() == OrderStatus.SHIPPED){
            throw new ConflictException("No se puede cancelar un pedido enviado");
        }

        if (order.getStatus() == OrderStatus.CANCELLED){
            throw new ConflictException("No se puede cancelar un pedido cancelado");
        }



        if(order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.PREPARING){



            for (OrderLine line : order.getOrderLines()){

                int newStock = line.getProduct().getStock() + line.getQuantity();

                line.getProduct().setStock(newStock);

            }

        }


        order.setStatus(OrderStatus.CANCELLED);



        Order savedOrder = orderRepository.save(order);

        return orderMapper.toAdminResponse(savedOrder);


    }


    @Transactional(readOnly = true)
    public List<OrderAdminResponse> getAllOrders(){

        return orderRepository.findAll().stream()
                .map(orderMapper::toAdminResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<OrderAdminResponse> getOrdersByCustomerId (Long id){

        findCustomerOrThrow(id);

        return orderRepository.findByCustomerId(id).stream()
                .map(orderMapper::toAdminResponse)
                .toList();
    }



    private Order findOrderOrThrow (Long id){

        return orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Order with id:" + id + " not found"));

    }

    private Customer findCustomerOrThrow(Long id){
         return customerRespository.findById(id)
                .orElseThrow(()-> new NotFoundException("Customer with id: " + id + " not found"));
    }

}
