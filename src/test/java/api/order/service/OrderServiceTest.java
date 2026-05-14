package api.order.service;


import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.customer.repository.CustomerRepository;
import com.ordertrack.orderdertrack.api.order.mapper.OrderMapper;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderAdminResponse;
import com.ordertrack.orderdertrack.api.order.model.dto.OrderCreateRequest;
import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import com.ordertrack.orderdertrack.api.order.model.enums.OrderStatus;
import com.ordertrack.orderdertrack.api.order.repository.OrderRepository;
import com.ordertrack.orderdertrack.api.order.service.OrderService;
import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import com.ordertrack.orderdertrack.api.orderline.repository.OrderLineRepository;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @Mock
    OrderRepository orderRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    OrderLineRepository orderLineRepository;
    @Mock
    OrderMapper orderMapper;

    @InjectMocks
    OrderService orderService;



    @Test
    void createOrder_shouldCreateOrder_whenCustomerExists(){

        OrderCreateRequest request = new OrderCreateRequest(1L);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order savedOrder = new Order(customer);

        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(1L, 1L, "pepe", "pepe@gmail.com", OrderStatus.DRAFT, BigDecimal.ZERO, null, null);


        when(customerRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toAdminResponse(savedOrder)).thenReturn(orderAdminResponse);



        OrderAdminResponse result = orderService.createOrder(request);


        assertNotNull(result);
        assertEquals(orderAdminResponse.status(), result.status());
        assertEquals(orderAdminResponse.customerId(), result.customerId());
        assertEquals(orderAdminResponse.total(), result.total());



        verify(customerRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toAdminResponse(savedOrder);


    }

    @Test
    void createOrder_shouldThrowNotFoundException_whenCustomerDoesNotExist(){


        OrderCreateRequest request = new OrderCreateRequest(1L);

        when(customerRepository.findById(request.customerId())).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, ()->
            orderService.createOrder(request));



        verify(customerRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderMapper);


    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists(){

        Long id = 1L;


        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);


        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(1L, 1L, "pepe", "pepe@gmail.com", OrderStatus.DRAFT, BigDecimal.ZERO, null, null);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.toAdminResponse(order)).thenReturn(orderAdminResponse);

        OrderAdminResponse result = orderService.getOrderById(id);


        assertEquals(orderAdminResponse, result);

        verify(orderRepository).findById(id);
        verify(orderMapper).toAdminResponse(order);


    }


    @Test
    void getOrderById_shouldThrowNotFoundException_whenOrderDoesNotExist(){

        Long id = 1L;

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()->
            orderService.getOrderById(id));


        verify(orderRepository).findById(id);
        verifyNoInteractions(orderMapper);

    }

    @Test
    void getOrdersByCustomer_shouldReturnOrders_whenCustomerExixts(){

        Long id =  1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");



        Order order1 = new Order(customer);

        Order order2 = new Order(customer);

        List<Order> orders =  new ArrayList<>();

        OrderAdminResponse orderAdminResponse1 = new OrderAdminResponse(
                1L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.DRAFT,
                BigDecimal.ZERO,
                null,
                null);

        OrderAdminResponse orderAdminResponse2 = new OrderAdminResponse(
                2L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.CONFIRMED,
                BigDecimal.valueOf(49.99),
                null,
                null
        );

        orders.add(order1);
        orders.add(order2);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        when(orderRepository.findByCustomerId(id)).thenReturn(orders);

        when(orderMapper.toAdminResponse(order1)).thenReturn(orderAdminResponse1);
        when(orderMapper.toAdminResponse(order2)).thenReturn(orderAdminResponse2);


        List<OrderAdminResponse> result = orderService.getOrdersByCustomerId(id);

        assertEquals(orderAdminResponse1, result.get(0));
        assertEquals(orderAdminResponse2, result.get(1));



        verify(customerRepository).findById(id);
        verify(orderRepository).findByCustomerId(id);

        verify(orderMapper).toAdminResponse(order1);
        verify(orderMapper).toAdminResponse(order2);

    }

    @Test
    void getOrdersByCustomer_shouldThrowNotFoundException_whenCustomerDoesNotExist(){

        Long id =  1L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()->orderService.getOrdersByCustomerId(id));

        verify(customerRepository).findById(id);
        verify(orderRepository, never()).findByCustomerId(id);
        verifyNoInteractions(orderMapper);


    }


    @Test
    void confirmOrder_shouldConfirmOrder_whenOrderIsDraftAndHasLinesAndStock(){

        Long id = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 5);

        order.getOrderLines().add(line);


        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(
                1L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.CONFIRMED,
                new BigDecimal("99.50"),
                null,
                null);



        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toAdminResponse(order)).thenReturn(orderAdminResponse);

        OrderAdminResponse result = orderService.confirmOrder(id);

        assertEquals(orderAdminResponse, result);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(0, orderAdminResponse.total().compareTo(order.getTotal()));
        assertEquals(5, product.getStock());



        verify(orderRepository).findById(id);
        verify(orderRepository).save(order);
        verify(orderMapper).toAdminResponse(order);



    }


    @Test
    void confirmOrder_shouldThrowConflictException_whenOrderIsNotDraft(){

        Long id = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CONFIRMED);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, ()-> orderService.confirmOrder(id));

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderMapper);

    }


    @Test
    void confirmOrder_shouldThrowConflictException_whenOrderHasNoLines(){

        Long id = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, ()-> orderService.confirmOrder(id));

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderMapper);


    }


    @Test
    void confirmOrder_shouldThrowConflictException_whenProductHasNotEnoughStock(){

        Long id = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 20);

        order.getOrderLines().add(line);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, ()->orderService.confirmOrder(id));

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(orderMapper);

    }

    @Test
    void cancelOrder_shouldCancelDraftOrder_withoutRestoringStock(){

        Long id  = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        int initialStock = product.getStock();

        OrderLine line = new OrderLine(order, product, 5);

        order.getOrderLines().add(line);

        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(
                1L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.CANCELLED,
                new BigDecimal("99.50"),
                null,
                null);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toAdminResponse(order)).thenReturn(orderAdminResponse);


        OrderAdminResponse result = orderService.cancelOrder(id);

        assertEquals(orderAdminResponse, result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(initialStock, product.getStock() );

        verify(orderRepository).findById(id);
        verify(orderRepository).save(order);
        verify(orderMapper).toAdminResponse(order);


    }

    @Test
    void cancelOrder_shouldCancelConfirmedOrder_andRestoreStock(){

        Long id  = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CONFIRMED);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        int initialStock = product.getStock();

        OrderLine line = new OrderLine(order, product, 5);



        order.getOrderLines().add(line);

        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(
                1L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.CANCELLED,
                new BigDecimal("99.50"),
                null,
                null);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toAdminResponse(order)).thenReturn(orderAdminResponse);

        OrderAdminResponse result = orderService.cancelOrder(id);


        assertEquals(orderAdminResponse, result);
        assertEquals(initialStock + line.getQuantity() ,  product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

    }

    @Test
    void cancelOrder_shouldCancelPreparingOrder_andRestoreStock(){




        Long id  = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.PREPARING);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        int initialStock = product.getStock();

        OrderLine line = new OrderLine(order, product, 5);



        order.getOrderLines().add(line);

        OrderAdminResponse orderAdminResponse = new OrderAdminResponse(
                1L,
                1L,
                "pepe",
                "pepe@gmail.com",
                OrderStatus.CANCELLED,
                new BigDecimal("99.50"),
                null,
                null);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toAdminResponse(order)).thenReturn(orderAdminResponse);

        OrderAdminResponse result = orderService.cancelOrder(id);


        assertEquals(orderAdminResponse, result);
        assertEquals(initialStock + line.getQuantity() ,  product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());


    }

    @Test
    void cancelOrder_shouldThrowConflictException_whenOrderIsShipped(){


        Long id  = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.SHIPPED);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));


        assertThrows(ConflictException.class, () -> orderService.cancelOrder(id));


        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(order);
        verifyNoInteractions(orderMapper);
    }


    @Test
    void cancelOrder_shouldThrowConflictException_whenOrderIsAlreadyCancelled (){


        Long id  = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CANCELLED);


        when(orderRepository.findById(id)).thenReturn(Optional.of(order));


        assertThrows(ConflictException.class, () -> orderService.cancelOrder(id));

        verify(orderRepository).findById(id);
        verify(orderRepository, never()).save(order);
        verifyNoInteractions(orderMapper);

    }




















}
