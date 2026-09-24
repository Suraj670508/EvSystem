package com.voltgrid.billing.dto;

public class CreateBillingRequest {
    private Long sessionId;
    private Long userId;
    private Double energyConsumed;
    private Double customRate;

    public CreateBillingRequest() {}

    public CreateBillingRequest(Long sessionId, Long userId, Double energyConsumed, Double customRate) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.energyConsumed = energyConsumed;
        this.customRate = customRate;
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

    public Double getCustomRate() {
        return customRate;
    }

    public void setCustomRate(Double customRate) {
        this.customRate = customRate;
    }
}
