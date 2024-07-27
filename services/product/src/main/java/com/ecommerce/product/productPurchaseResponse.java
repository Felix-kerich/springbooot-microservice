package com.ecommerce.product;

import java.math.BigDecimal;

public record productPurchaseResponse(
    Integer productId,
    String name,
    String description,
    BigDecimal price,
    double quantity

) {
} 
