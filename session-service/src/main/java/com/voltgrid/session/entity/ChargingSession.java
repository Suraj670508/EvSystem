package com.voltgrid.session.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "charging_sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long stationId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double energyConsumed; // in kWh

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(nullable = false)
    private Double chargingRate; // in kW

    public ChargingSession() {}

    public ChargingSession(Long userId, Long stationId, Double chargingRate) {
        this.userId = userId;
        this.stationId = stationId;
        this.startTime = LocalDateTime.now();
        this.energyConsumed = 0.0;
        this.status = SessionStatus.ACTIVE;
        this.chargingRate = chargingRate != null ? chargingRate : 50.0;
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
