package com.voltgrid.session.dto;

public class EnergyUpdateRequest {
    private Double energyConsumed;

    public EnergyUpdateRequest() {}

    public EnergyUpdateRequest(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public Double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }
}
