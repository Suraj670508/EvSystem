package com.voltgrid.session.client;

import com.voltgrid.session.client.dto.BillingDto;
import com.voltgrid.session.client.dto.CreateBillingRequest;
import com.voltgrid.session.client.dto.UpdateEnergyBillingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "BILLING-SERVICE")
public interface BillingClient {

    @PostMapping("/billing")
    BillingDto createBilling(@RequestBody CreateBillingRequest request);

    @PutMapping("/billing/session/{sessionId}/energy")
    BillingDto updateEnergy(
            @PathVariable("sessionId") Long sessionId,
            @RequestBody UpdateEnergyBillingRequest request);
}
