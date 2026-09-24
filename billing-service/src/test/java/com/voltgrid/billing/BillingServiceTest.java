package com.voltgrid.billing;

import com.voltgrid.billing.dto.BillingDto;
import com.voltgrid.billing.dto.CreateBillingRequest;
import com.voltgrid.billing.entity.Billing;
import com.voltgrid.billing.entity.BillingStatus;
import com.voltgrid.billing.exception.ResourceNotFoundException;
import com.voltgrid.billing.repository.BillingRepository;
import com.voltgrid.billing.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository repository;

    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(repository, 15.0);
    }

    @Test
    void testCreateBilling() {
        CreateBillingRequest request = new CreateBillingRequest(100L, 1L, 10.0, null);
        Billing saved = new Billing(100L, 1L, 10.0, 15.0, 150.0, BillingStatus.PENDING);
        saved.setId(1L);

        when(repository.save(any(Billing.class))).thenReturn(saved);

        BillingDto result = billingService.createBilling(request);
        assertNotNull(result);
        assertEquals(150.0, result.getAmount());
        assertEquals(15.0, result.getRate());
        assertEquals(BillingStatus.PENDING, result.getBillingStatus());
    }

    @Test
    void testUpdateEnergyRecalculatesAmount() {
        Billing billing = new Billing(100L, 1L, 5.0, 15.0, 75.0, BillingStatus.PENDING);
        billing.setId(1L);

        when(repository.findBySessionId(100L)).thenReturn(Optional.of(billing));
        when(repository.save(any(Billing.class))).thenAnswer(i -> i.getArgument(0));

        BillingDto updated = billingService.updateEnergy(100L, 20.0);
        assertEquals(20.0, updated.getEnergyConsumed());
        assertEquals(300.0, updated.getAmount());
    }

    @Test
    void testPayBillTransitionsToPaid() {
        Billing billing = new Billing(100L, 1L, 10.0, 15.0, 150.0, BillingStatus.PENDING);
        billing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(billing));
        when(repository.save(any(Billing.class))).thenAnswer(i -> i.getArgument(0));

        BillingDto paid = billingService.payBill(1L);
        assertEquals(BillingStatus.PAID, paid.getBillingStatus());
    }

    @Test
    void testBillingNotFoundThrows404() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> billingService.getBillingById(999L));
    }
}
