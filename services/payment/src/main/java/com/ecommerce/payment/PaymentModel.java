package com.ecommerce.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String merchantRequestId;
    private String checkoutRequestId;
    private String mpesaReceiptNumber;
    private String phoneNumber;
    private String username;
    private double amount;
    private String product;
    private String transactionCode;
    private int resultCode;
    private String status;
    @Column(nullable = false, unique = true)
    private Long productId;



   

}
