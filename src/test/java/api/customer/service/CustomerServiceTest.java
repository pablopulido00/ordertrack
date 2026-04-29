package api.customer.service;

import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerUpdateRequest;
import com.ordertrack.orderdertrack.api.customer.service.CustomerService;
import com.ordertrack.orderdertrack.api.customer.mapper.CustomerMapper;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerCreateRequest;
import com.ordertrack.orderdertrack.api.customer.model.dto.CustomerResponse;
import com.ordertrack.orderdertrack.api.customer.model.entity.Customer;
import com.ordertrack.orderdertrack.api.customer.repository.CustomerRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    CustomerService customerService;


    @Test
    void createCustomer_shouldCreateCustomer_whenEmailDoesNotExist(){

     //Arrange
        CustomerCreateRequest request = new CustomerCreateRequest("Pepe", "123456", "pep@pepe.com");
        Customer customer = new Customer("Pepe", "123456", "pep@pepe.com");
        CustomerResponse response = new CustomerResponse(1L, 1L, "123456", "123456", "pep@pepe.com" );


        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

    //Act

        CustomerResponse result = customerService.createCustomer(request);

    //Assert
    assertEquals(result, response);

    }

    @Test
    void createCustomer_shouldThrowConflictException_whenEmailAlreadyExists() {

        //Arrenge

        CustomerCreateRequest request = new CustomerCreateRequest("Pepe", "123456", "pep@pepe.com");
        ;


        when(customerRepository.existsByEmail(request.email())).thenReturn(true);


        //Act + Assert
        assertThrows(ConflictException.class, () -> {
            customerService.createCustomer(request);
        });
    }

        @Test
        void getCustomerById_shouldThrowNotFoundException_whenDoesNotExist(){

            //Arrange
            Long id = 1L;


            when(customerRepository.findById(id)).thenReturn(Optional.empty());



            //Act + Assert
            assertThrows(NotFoundException.class, () ->{
                customerService.getCustomerById(id);
            });



        }


        @Test
        void updateCustomer_shouldUpdateCustomer_whenRequestIsValid(){

            Long id = 1L;
            Customer customer = new Customer("Pepe", "123456", "pep@pepe.com");
            CustomerUpdateRequest request = new CustomerUpdateRequest("Pepe", "123456", "pep@pepe.com");
            CustomerResponse response = new CustomerResponse(1L, 1L, "Ramon", "123456", "pep@pepe.com" );

            when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
            when(customerRepository.save(customer)).thenReturn(customer);
            when(customerMapper.toResponse(customer)).thenReturn(response);



            CustomerResponse result = customerService.updateCustomer(id, request);

            assertEquals(result, response);



        }


        @Test
        void updateCustomer_shouldTrhowConflictExceotion_whenEmailDuplicate(){

            Long id = 1l;
            Customer customer = new Customer("Pepe", "123456", "pep@pepe.com");
            CustomerUpdateRequest request = new CustomerUpdateRequest("Ramon", "1234567", "otro@pepe.com");


            when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

            when(customerRepository.existsByEmail(request.email())).thenReturn(true);

            assertThrows(ConflictException.class, ()->{
               customerService.updateCustomer(id, request);
            });



        }

        @Test
        void deleteCustomer_shouldDeleteCustomer(){

            Long id = 1L;

            Customer customer = new Customer("Pepe", "123456", "pep@pepe.com");

            when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

            customerService.deleteCustomer(id);


            verify(customerRepository).delete(customer);
        }








    }




