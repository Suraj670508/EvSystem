package com.voltgrid.billing.dto;

public class UpdateEnergyBillingRequest {
    private Double energyConsumed;

    public UpdateEnergyBillingRequest() {}

    public UpdateEnergyBillingRequest(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public Double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }
}
