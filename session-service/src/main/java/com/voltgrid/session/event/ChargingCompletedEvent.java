package com.voltgrid.session.event;

public class ChargingCompletedEvent {
    private final Long sessionId;
    private final Long stationId;
    private final Double totalEnergy;

    public ChargingCompletedEvent(Long sessionId, Long stationId, Double totalEnergy) {
        this.sessionId = sessionId;
        this.stationId = stationId;
        this.totalEnergy = totalEnergy;
    }

    public Long getSessionId() { return sessionId; }
    public Long getStationId() { return stationId; }
    public Double getTotalEnergy() { return totalEnergy; }
}
