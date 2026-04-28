package com.ordertrack.orderdertrack.api.product.service;


import com.ordertrack.orderdertrack.api.product.mapper.ProductMapper;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductAdminResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductCreateRequest;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductPublicResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductUpdateRequest;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import com.ordertrack.orderdertrack.api.product.repository.ProductRepository;
import com.ordertrack.orderdertrack.common.exception.ConflictException;
import com.ordertrack.orderdertrack.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

     private final ProductRepository productRepository;
     private final ProductMapper productMapper;



     @Transactional(readOnly = true)
    public List<ProductPublicResponse> getAllActive(){
        return productRepository.findByActiveTrue()
                .stream().map(productMapper::toPublicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductAdminResponse> getAll() {
        return productRepository.findAll()
                .stream().map(productMapper::toAdminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductPublicResponse getActiveById(Long id){

        Product product = findProductOrThrow(id);

        if (!product.getActive()){
            throw new NotFoundException("Product with id = " + id + " does not exist");
        }

        return  productMapper.toPublicResponse(product);

    }

    @Transactional(readOnly = true)
    public ProductAdminResponse getById(Long id){

        Product product = findProductOrThrow(id);


        return  productMapper.toAdminResponse(product);

    }


    public ProductAdminResponse createProduct (ProductCreateRequest request){

        Product product = new Product(
                request.name(),
                request.price(),
                request.stock(),
                request.active()
        );

        Product productSaved = productRepository.save(product);

        return productMapper.toAdminResponse(productSaved);

    }

    public ProductAdminResponse updateProduct (Long id, ProductUpdateRequest request){

        Product product = findProductOrThrow(id);

        if (request.name() != null){
            product.setName(request.name());
        }

        if (request.price() != null){
            product.setPrice(request.price());
        }


        if(request.stock() != null){
            product.setStock(request.stock());
        }

        Product productUpdated = productRepository.save(product);


        return productMapper.toAdminResponse(productUpdated);

    }

    public ProductAdminResponse activateProduct(Long id){

        Product product = findProductOrThrow(id);

        if(product.getActive()){
            throw new ConflictException("Product already active");
        }
        product.setActive(true);

        Product productUpdated = productRepository.save(product);

        return productMapper.toAdminResponse(productUpdated);


    }

    public ProductAdminResponse deactivateProduct(Long id){

        Product product = findProductOrThrow(id);

        if(!product.getActive()){
            throw new ConflictException("Product already inactive");
        }
        product.setActive(false);

        Product productUpdated = productRepository.save(product);

        return productMapper.toAdminResponse(productUpdated);



    }

    private Product findProductOrThrow(Long id){

         return productRepository.findById(id)
                 .orElseThrow(()-> new NotFoundException("Product with id = " + id + " does not exist"));

    }

}
