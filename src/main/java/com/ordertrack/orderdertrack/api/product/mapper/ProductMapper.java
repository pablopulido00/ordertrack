package com.ordertrack.orderdertrack.api.product.mapper;

import com.ordertrack.orderdertrack.api.product.model.dto.ProductAdminResponse;
import com.ordertrack.orderdertrack.api.product.model.dto.ProductPublicResponse;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductPublicResponse toPublicResponse(Product product){

        return new ProductPublicResponse(product.getId(), product.getName(), product.getPrice());
    }

    public ProductAdminResponse toAdminResponse(Product product){

        return new ProductAdminResponse(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getActive());
    }
}
