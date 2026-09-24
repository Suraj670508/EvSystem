package com.voltgrid.billing.service;

import com.voltgrid.billing.dto.BillingDto;
import com.voltgrid.billing.dto.CreateBillingRequest;
import com.voltgrid.billing.entity.Billing;
import com.voltgrid.billing.entity.BillingStatus;
import com.voltgrid.billing.exception.ResourceNotFoundException;
import com.voltgrid.billing.repository.BillingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final BillingRepository repository;
    private final double defaultRate;

    public BillingService(
            BillingRepository repository,
            @Value("${billing.default-rate:15.0}") double defaultRate) {
        this.repository = repository;
        this.defaultRate = defaultRate;
    }

    @Transactional
    public BillingDto createBilling(CreateBillingRequest request) {
        if (request.getSessionId() == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }

        double rate = request.getCustomRate() != null && request.getCustomRate() > 0
                ? request.getCustomRate()
                : defaultRate;
        double energy = request.getEnergyConsumed() != null ? request.getEnergyConsumed() : 0.0;
        double amount = Math.round(energy * rate * 100.0) / 100.0;

        Billing billing = new Billing(
                request.getSessionId(),
                request.getUserId(),
                energy,
                rate,
                amount,
                BillingStatus.PENDING
        );

        Billing saved = repository.save(billing);
        return mapToDto(saved);
    }

    public List<BillingDto> getAllBillings() {
        return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public BillingDto getBillingById(Long id) {
        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));
        return mapToDto(billing);
    }

    public BillingDto getBillingBySessionId(Long sessionId) {
        Billing billing = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for session id: " + sessionId));
        return mapToDto(billing);
    }

    @Transactional
    public BillingDto updateEnergy(Long sessionId, Double energyConsumed) {
        if (energyConsumed == null || energyConsumed < 0) {
            throw new IllegalArgumentException("Energy consumed must be greater than or equal to 0");
        }

        Billing billing = repository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for session id: " + sessionId));

        billing.setEnergyConsumed(energyConsumed);
        double amount = Math.round(energyConsumed * billing.getRate() * 100.0) / 100.0;
        billing.setAmount(amount);

        Billing saved = repository.save(billing);
        return mapToDto(saved);
    }

    @Transactional
    public BillingDto updateBilling(Long id, BillingDto dto) {
        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));

        if (dto.getEnergyConsumed() != null) {
            billing.setEnergyConsumed(dto.getEnergyConsumed());
            billing.setAmount(Math.round(dto.getEnergyConsumed() * billing.getRate() * 100.0) / 100.0);
        }
        if (dto.getRate() != null) {
            billing.setRate(dto.getRate());
            billing.setAmount(Math.round(billing.getEnergyConsumed() * dto.getRate() * 100.0) / 100.0);
        }
        if (dto.getBillingStatus() != null) {
            billing.setBillingStatus(dto.getBillingStatus());
        }

        Billing saved = repository.save(billing);
        return mapToDto(saved);
    }

    @Transactional
    public BillingDto payBill(Long id) {
        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));
        billing.setBillingStatus(BillingStatus.PAID);
        Billing saved = repository.save(billing);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteBilling(Long id) {
        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + id));
        repository.delete(billing);
    }

    private BillingDto mapToDto(Billing billing) {
        return new BillingDto(
                billing.getId(),
                billing.getSessionId(),
                billing.getUserId(),
                billing.getEnergyConsumed(),
                billing.getRate(),
                billing.getAmount(),
                billing.getBillingStatus()
        );
    }
}
