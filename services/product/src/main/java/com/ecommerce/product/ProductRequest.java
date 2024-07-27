package com.ecommerce.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
     Integer id,

     @NotNull(message = "prodduct name is required")
     String name,

     @NotNull(message = "prodduct description is required")
     String description,

     @Positive(message = "Available Quantity shuld be positive")
     double availableQuantity,

     @Positive(message = "price shuld be positive")
     BigDecimal price,
      
     @NotNull(message = "product category is required")
     Integer categoryId  
){

} 