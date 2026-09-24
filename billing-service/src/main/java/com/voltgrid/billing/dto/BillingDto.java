package com.voltgrid.billing.dto;

import com.voltgrid.billing.entity.BillingStatus;

public class BillingDto {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Double energyConsumed;
    private Double rate;
    private Double amount;
    private BillingStatus billingStatus;

    public BillingDto() {}

    public BillingDto(Long id, Long sessionId, Long userId, Double energyConsumed, Double rate, Double amount, BillingStatus billingStatus) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.energyConsumed = energyConsumed;
        this.rate = rate;
        this.amount = amount;
        this.billingStatus = billingStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public BillingStatus getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(BillingStatus billingStatus) {
        this.billingStatus = billingStatus;
    }
}
