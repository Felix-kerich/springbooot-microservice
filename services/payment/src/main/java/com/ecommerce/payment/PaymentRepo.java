package com.ecommerce.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentModel,Long>{


}
