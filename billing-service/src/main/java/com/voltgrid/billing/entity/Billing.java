package com.voltgrid.billing.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "billings")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    private Long userId;

    @Column(nullable = false)
    private Double energyConsumed;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus billingStatus;

    public Billing() {}

    public Billing(Long sessionId, Long userId, Double energyConsumed, Double rate, Double amount, BillingStatus billingStatus) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.energyConsumed = energyConsumed != null ? energyConsumed : 0.0;
        this.rate = rate != null ? rate : 15.0;
        this.amount = amount != null ? amount : 0.0;
        this.billingStatus = billingStatus != null ? billingStatus : BillingStatus.PENDING;
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
