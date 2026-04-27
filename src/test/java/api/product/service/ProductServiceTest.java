package api.product.service;

import com.ordertrack.orderdertrack.api.product.mapper.ProductMapper;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductAdminResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductCreateRequest;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductPublicResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductUpdateRequest;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.api.product.repository.ProductRepository;
import com.ordertrack.orderdertrack.api.product.service.ProductService;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void getActiveById_shouldReturnPublicResponse_whenProductIsActive() {
        Long productId = 1L;
        Product product = new Product("Keyboard", new BigDecimal("49.99"), 10, true);
        ProductPublicResponse response = mock(ProductPublicResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toPublicResponse(product)).thenReturn(response);

        ProductPublicResponse result = productService.getActiveById(productId);

        assertEquals(response, result);
        verify(productRepository).findById(productId);
        verify(productMapper).toPublicResponse(product);
    }

    @Test
    void getActiveById_shouldThrowNotFoundException_whenProductIsInactive() {
        Long productId = 1L;
        Product product = new Product("Keyboard", new BigDecimal("49.99"), 10, false);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productService.getActiveById(productId));

        verify(productRepository).findById(productId);
        verify(productMapper, never()).toPublicResponse(any());
    }

    @Test
    void createProduct_shouldCreateAndReturnAdminResponse() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Keyboard",
                new BigDecimal("49.99"),
                10,
                true
        );

        Product savedProduct = new Product("Keyboard", new BigDecimal("49.99"), 10, true);
        ProductAdminResponse response = mock(ProductAdminResponse.class);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toAdminResponse(savedProduct)).thenReturn(response);

        ProductAdminResponse result = productService.createProduct(request);

        assertEquals(response, result);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toAdminResponse(savedProduct);
    }

    @Test
    void updateProduct_shouldUpdateOnlyNonNullFields() {
        Long productId = 1L;
        Product product = new Product("Old name", new BigDecimal("20.00"), 5, true);

        ProductUpdateRequest request = new ProductUpdateRequest(
                "New name",
                new BigDecimal("25.00"),
                8
        );

        ProductAdminResponse response = mock(ProductAdminResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toAdminResponse(product)).thenReturn(response);

        ProductAdminResponse result = productService.updateProduct(productId, request);

        assertEquals("New name", product.getName());
        assertEquals(new BigDecimal("25.00"), product.getPrice());
        assertEquals(8, product.getStock());
        assertEquals(response, result);

        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(productMapper).toAdminResponse(product);
    }

    @Test
    void activateProduct_shouldActivateProductAndReturnResponse() {
        Long productId = 1L;
        Product product = new Product("Keyboard", new BigDecimal("49.99"), 10, false);
        ProductAdminResponse response = mock(ProductAdminResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toAdminResponse(product)).thenReturn(response);

        ProductAdminResponse result = productService.activateProduct(productId);

        assertTrue(product.getActive());
        assertEquals(response, result);

        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(productMapper).toAdminResponse(product);
    }

    @Test
    void activateProduct_shouldThrowConflictException_whenProductIsAlreadyActive() {
        Long productId = 1L;
        Product product = new Product("Keyboard", new BigDecimal("49.99"), 10, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ConflictException.class, () -> productService.activateProduct(productId));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toAdminResponse(any());
    }

    @Test
    void deactivateProduct_shouldDeactivateProductAndReturnResponse() {
        Long productId = 1L;
        Product product = new Product("Keyboard", new BigDecimal("49.99"), 10, true);
        ProductAdminResponse response = mock(ProductAdminResponse.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toAdminResponse(product)).thenReturn(response);

        ProductAdminResponse result = productService.deactivateProduct(productId);

        assertFalse(product.getActive());
        assertEquals(response, result);

        verify(productRepository).findById(productId);
        verify(productRepository).save(product);
        verify(productMapper).toAdminResponse(product);
    }
}