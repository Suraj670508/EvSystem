package com.voltgrid.billing.repository;

import com.voltgrid.billing.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Optional<Billing> findBySessionId(Long sessionId);
}
