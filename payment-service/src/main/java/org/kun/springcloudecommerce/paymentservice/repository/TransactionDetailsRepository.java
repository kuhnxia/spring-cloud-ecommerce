package org.kun.springcloudecommerce.paymentservice.repository;

import org.kun.springcloudecommerce.paymentservice.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
}
