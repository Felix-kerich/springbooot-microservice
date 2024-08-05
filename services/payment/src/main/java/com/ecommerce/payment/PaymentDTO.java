package com.ecommerce.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentDTO {
   
    private Long id;
    private String merchantRequestId;
    private String checkoutRequestId;
    private String mpesaReceiptNumber;
    private Long phoneNumber;
    private String username;
    private double amount;
    private String product;  
    private Long productId;
    private int resultCode;
    private String transactionCode;
    private String status;



}
