package com.ecommerce.product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.exception.ProductPurchaseExceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(ProductRequest request){
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<productPurchaseResponse> purchaseProducts(List<ProductPurchseRequest> request) {
        var productIds = request
                  .stream()
                  .map( ProductPurchseRequest::productId)
                  .toList();

        var storedProducts = repository.findAllByIdInOrderById(productIds);  
        if (productIds.size() !=  storedProducts.size()) {
            throw new ProductPurchaseExceptions("One of the products does not exists");
            
        } 
        var storedRequest = request
                   .stream()
                   .sorted(Comparator.comparing(ProductPurchseRequest::productId))   
                   .toList();
        var purchasedProducts = new ArrayList<productPurchaseResponse>();  
        for(int i = 0; i < storedProducts.size(); i++)  {
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);
            if(product.getAvailableQuantity() < productRequest.quantity()){
                throw new ProductPurchaseExceptions("Insufficient stock quantity for product with ID: " );
            }
            var newAvailabeQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailabeQuantity);
            repository.save(product);

            purchasedProducts.add(mapper.toProductPurchseResponse(product,productRequest.quantity()));

        }            
       return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
        .map(mapper::toProductResponse)
        .orElseThrow(() -> new EntityNotFoundException("product not found with ID: " + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
             .stream()
             .map(mapper::toProductResponse)
             .collect(Collectors.toList());
    }




}
