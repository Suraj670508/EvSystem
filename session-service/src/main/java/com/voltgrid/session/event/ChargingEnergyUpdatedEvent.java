package com.voltgrid.session.event;

public class ChargingEnergyUpdatedEvent {
    private final Long sessionId;
    private final Double energyConsumed;

    public ChargingEnergyUpdatedEvent(Long sessionId, Double energyConsumed) {
        this.sessionId = sessionId;
        this.energyConsumed = energyConsumed;
    }

    public Long getSessionId() { return sessionId; }
    public Double getEnergyConsumed() { return energyConsumed; }
}
