package com.voltgrid.session.dto;

public class CreateSessionRequest {
    private Long userId;
    private Long stationId;
    private Double chargingRate;

    public CreateSessionRequest() {}

    public CreateSessionRequest(Long userId, Long stationId, Double chargingRate) {
        this.userId = userId;
        this.stationId = stationId;
        this.chargingRate = chargingRate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Double getChargingRate() {
        return chargingRate != null ? chargingRate : 50.0;
    }

    public void setChargingRate(Double chargingRate) {
        this.chargingRate = chargingRate;
    }
}
