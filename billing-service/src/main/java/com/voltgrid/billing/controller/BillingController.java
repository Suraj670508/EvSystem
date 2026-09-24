package com.voltgrid.billing.controller;

import com.voltgrid.billing.dto.BillingDto;
import com.voltgrid.billing.dto.CreateBillingRequest;
import com.voltgrid.billing.dto.UpdateEnergyBillingRequest;
import com.voltgrid.billing.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BillingDto> createBilling(@RequestBody CreateBillingRequest request) {
        BillingDto created = service.createBilling(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BillingDto>> getAllBillings() {
        return ResponseEntity.ok(service.getAllBillings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingDto> getBillingById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getBillingById(id));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<BillingDto> getBillingBySessionId(@PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(service.getBillingBySessionId(sessionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingDto> updateBilling(@PathVariable("id") Long id, @RequestBody BillingDto dto) {
        return ResponseEntity.ok(service.updateBilling(id, dto));
    }

    @PutMapping("/session/{sessionId}/energy")
    public ResponseEntity<BillingDto> updateEnergy(
            @PathVariable("sessionId") Long sessionId,
            @RequestBody UpdateEnergyBillingRequest request) {
        return ResponseEntity.ok(service.updateEnergy(sessionId, request.getEnergyConsumed()));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillingDto> payBill(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.payBill(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBilling(@PathVariable("id") Long id) {
        service.deleteBilling(id);
        return ResponseEntity.ok(Map.of("message", "Billing record deleted successfully"));
    }
}
