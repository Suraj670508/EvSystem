package com.voltgrid.session.event;

public class ChargingStartedEvent {
    private final Long sessionId;
    private final Long userId;
    private final Long stationId;

    public ChargingStartedEvent(Long sessionId, Long userId, Long stationId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.stationId = stationId;
    }

    public Long getSessionId() { return sessionId; }
    public Long getUserId() { return userId; }
    public Long getStationId() { return stationId; }
}
