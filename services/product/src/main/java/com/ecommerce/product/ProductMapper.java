package com.ecommerce.product;

import org.springframework.stereotype.Service;

import com.ecommerce.product.category.Category;

import jakarta.validation.constraints.NotNull;

@Service
public class ProductMapper {

    public Product toProduct(ProductRequest request) {
        return Product.builder()
        .id(request.id())
        .name(request.name())
        .description(request.description())
        .price(request.price())
        .availableQuantity(request.availableQuantity())
        .category(
            Category.builder()
                    .id(request.categoryId())
                    .build() 
        )
        .build();

    }

    public ProductResponse toProductResponse(Product product){
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getAvailableQuantity(),
            product.getPrice(),
            product.getCategory().getId(),
            product.getCategory().getName(),
            product.getCategory().getDescription()
            
        );
    }

    public productPurchaseResponse toProductPurchseResponse(Product product,
            @NotNull(message = "Quantitty is mandatory") double quantity) {
       return new productPurchaseResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        quantity
               );
    }

}
