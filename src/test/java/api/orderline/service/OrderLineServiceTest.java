package api.orderline.service;


import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
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
import com.ordertrack.orderdertrack.api.orderline.service.OrderLineService;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.api.product.repository.ProductRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderLineServiceTest {


    @Mock
    OrderLineRepository orderLineRepository;
    @Mock
    OrderLineMapper orderLineMapper;
    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    OrderService orderService;

    @InjectMocks
    OrderLineService orderLineService;


    @Test
    void addLine_ShouldAddLine_whenOrderIsDraftAndProductExist(){

        Long orderId = 1L ;

        OrderLineCreateRequest request =
                new OrderLineCreateRequest(
                1L,
                5);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, request.quantity());

        OrderLineResponse lineResponse = new OrderLineResponse(
                1L,
                1L,
                1L,
                "camiseta",
                new BigDecimal("19.90"),
                5,
                new BigDecimal("99.50")

        );


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(product));
        when(orderLineRepository.existsByOrderIdAndProductId(orderId, request.productId()))
                .thenReturn(false);
        when(orderLineRepository.save(any(OrderLine.class))).thenReturn(line);
        when(orderLineMapper.toResponse(line)).thenReturn(lineResponse);


        OrderLineResponse result = orderLineService.addLine(orderId, request);

        assertEquals(lineResponse, result);


        ArgumentCaptor<OrderLine> orderLineCaptor = ArgumentCaptor.forClass(OrderLine.class);

        verify(orderLineRepository).save(orderLineCaptor.capture());

        OrderLine capturedLine = orderLineCaptor.getValue();

        assertEquals(order, capturedLine.getOrder());
        assertEquals(product, capturedLine.getProduct());
        assertEquals(5, capturedLine.getQuantity());
        assertEquals(new BigDecimal("99.50"), capturedLine.getLineTotal());




        verify(orderRepository).findById(orderId);
        verify(orderService).recalculateTotal(order);
        verify(productRepository).findById(request.productId());
        verify(orderLineRepository).existsByOrderIdAndProductId(orderId, request.productId());
        verify(orderLineMapper).toResponse(line);


    }

    @Test
    void addLine_shouldThrowConflictException_whenOrderIsNoDraft(){



        Long orderId = 1L;

        OrderLineCreateRequest request =
                new OrderLineCreateRequest(
                        1L,
                        5);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CONFIRMED);


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));


        assertThrows(ConflictException.class, ()-> orderLineService.addLine(orderId, request) );


        verify(orderRepository).findById(orderId);
        verify(orderService, never()).recalculateTotal(order);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(orderLineMapper);
        verify(orderLineRepository, never()).existsByOrderIdAndProductId(orderId, request.productId());
        verify(orderLineRepository, never()).save(any(OrderLine.class));

    }


    @Test
    void addLine_shouldThrowNotFoundException_whenProductDoesNotExist(){


        Long orderId = 1L;

        OrderLineCreateRequest request =
                new OrderLineCreateRequest(
                        1L,
                        5);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(request.productId())).thenReturn(Optional.empty());



        assertThrows(NotFoundException.class, ()-> orderLineService.addLine(orderId, request));


        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(request.productId());
        verify(orderService, never()).recalculateTotal(order);
        verifyNoInteractions(orderLineMapper);
        verify(orderLineRepository, never()).existsByOrderIdAndProductId(orderId, request.productId());
        verify(orderLineRepository, never()).save(any(OrderLine.class));
    }

    @Test
    void addLine_shouldThrowConflictException_whenProductAlreadyExistsInOrder(){

        Long orderId = 1L ;

        OrderLineCreateRequest request =
                new OrderLineCreateRequest(
                        1L,
                        5);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(product));
        when(orderLineRepository.existsByOrderIdAndProductId(orderId, request.productId())).thenReturn(true);



        assertThrows(ConflictException.class, ()->orderLineService.addLine(orderId, request));


        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(request.productId());
        verify(orderService, never()).recalculateTotal(order);
        verify(orderLineRepository).existsByOrderIdAndProductId(orderId, request.productId());
        verifyNoInteractions(orderLineMapper);
        verify(orderLineRepository, never()).save(any(OrderLine.class));
    }



    @Test
    void updateLine_shouldUpdateQuantityAndLineTotal_whenOrderIsDraft (){

        Long lineId = 1L;


        OrderLineUpdateRequest request = new OrderLineUpdateRequest(4);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 2);

        OrderLineResponse lineResponse = new OrderLineResponse(
                1L,
                1L,
                1L,
                "camiseta",
                new BigDecimal("19.90"),
                4,
                new BigDecimal("79.60")

        );

        when(orderLineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(orderLineRepository.save(line)).thenReturn(line);
        when(orderLineMapper.toResponse(line)).thenReturn(lineResponse);


        OrderLineResponse result = orderLineService.updateLine(lineId, request);

        assertEquals(lineResponse, result);
        assertEquals(4, line.getQuantity());
        assertEquals(new BigDecimal("79.60"), line.getLineTotal());


        verify(orderLineRepository).findById(lineId);
        verify(orderLineRepository).save(line);
        verify(orderService).recalculateTotal(order);
        verify(orderLineMapper).toResponse(line);


    }

    @Test
    void updateLine_shouldThrowConflictException_whenOrderIsNotDraft(){


        Long lineId = 1L;


        OrderLineUpdateRequest request = new OrderLineUpdateRequest(4);

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CONFIRMED);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 2);


        when(orderLineRepository.findById(lineId)).thenReturn(Optional.of(line));


        assertThrows(ConflictException.class, ()-> orderLineService.updateLine(lineId, request));



        verify(orderLineRepository).findById(lineId);
        verify(orderLineRepository, never()).save(any(OrderLine.class));
        verify(orderService, never()).recalculateTotal(order);
        verifyNoInteractions(orderLineMapper);

    }

    @Test
    void updateLine_shouldThrowNotFoundException_whenLineDoesNotExist(){


        Long lineId = 1L;

        OrderLineUpdateRequest request = new OrderLineUpdateRequest(4);

        when(orderLineRepository.findById(lineId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()-> orderLineService.updateLine(lineId, request));


        verify(orderLineRepository).findById(lineId);
        verify(orderLineRepository, never()).save(any(OrderLine.class));
        verifyNoInteractions(orderService);
        verifyNoInteractions(orderLineMapper);


    }

    @Test
    void deleteLine_shouldDeleteLine_whenOrderIsDraft(){

        Long lineId = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 2);


        when(orderLineRepository.findById(lineId)).thenReturn(Optional.of(line));

        orderLineService.deleteOrderLine(lineId);

        verify(orderLineRepository).findById(lineId);
        verify(orderService).recalculateTotal(order);
        verify(orderLineRepository).delete(line);

    }

    @Test
    void deleteLine_shouldThrowConflictException_whenOrderIsNotDraft(){


        Long lineId = 1L;

        Customer customer = new Customer("pepe", "123456", "pepe@gmail.com");

        Order order = new Order(customer);
        order.setStatus(OrderStatus.CONFIRMED);

        Product product = new Product("camiseta", new BigDecimal("19.90"), 10, true);

        OrderLine line = new OrderLine(order, product, 2);


        when(orderLineRepository.findById(lineId)).thenReturn(Optional.of(line));


        assertThrows(ConflictException.class, ()-> orderLineService.deleteOrderLine(lineId));


        verify(orderLineRepository).findById(lineId);
        verify(orderService, never()).recalculateTotal(any(Order.class));
        verify(orderLineRepository, never()).delete(any(OrderLine.class));

    }

    @Test
    void deleteLine_shouldThrowNotFoundException_whenLineDoesNotExist() {

        Long lineId = 1L;


        when(orderLineRepository.findById(lineId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderLineService.deleteOrderLine(lineId));


        verify(orderLineRepository).findById(lineId);
        verify(orderService, never()).recalculateTotal(any(Order.class));
        verify(orderLineRepository, never()).delete(any(OrderLine.class));


    }



}


