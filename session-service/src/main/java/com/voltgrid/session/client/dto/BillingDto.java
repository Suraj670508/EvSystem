package com.voltgrid.session.client.dto;

public class BillingDto {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Double energyConsumed;
    private Double rate;
    private Double amount;
    private String billingStatus;

    public BillingDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getEnergyConsumed() { return energyConsumed; }
    public void setEnergyConsumed(Double energyConsumed) { this.energyConsumed = energyConsumed; }
    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getBillingStatus() { return billingStatus; }
    public void setBillingStatus(String billingStatus) { this.billingStatus = billingStatus; }
}
