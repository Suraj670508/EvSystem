package com.voltgrid.session.dto;

import com.voltgrid.session.entity.SessionStatus;
import java.time.LocalDateTime;

public class SessionDto {
    private Long id;
    private Long userId;
    private Long stationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double energyConsumed;
    private SessionStatus status;
    private Double chargingRate;

    public SessionDto() {}

    public SessionDto(Long id, Long userId, Long stationId, LocalDateTime startTime, LocalDateTime endTime,
                      Double energyConsumed, SessionStatus status, Double chargingRate) {
        this.id = id;
        this.userId = userId;
        this.stationId = stationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.energyConsumed = energyConsumed;
        this.status = status;
        this.chargingRate = chargingRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Double getChargingRate() {
        return chargingRate;
    }

    public void setChargingRate(Double chargingRate) {
        this.chargingRate = chargingRate;
    }
}
