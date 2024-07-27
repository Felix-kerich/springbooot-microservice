package com.ecommerce.product;

import jakarta.validation.constraints.NotNull;

public record ProductPurchseRequest(
    @NotNull(message = "product is mandatory")
    Integer productId,
    @NotNull(message = "Quantitty is mandatory")
    double quantity
) {
}