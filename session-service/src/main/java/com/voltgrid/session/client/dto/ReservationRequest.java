package com.voltgrid.session.client.dto;

public class ReservationRequest {
    private Double chargingRate;

    public ReservationRequest() {}

    public ReservationRequest(Double chargingRate) {
        this.chargingRate = chargingRate;
    }

    public Double getChargingRate() { return chargingRate; }
    public void setChargingRate(Double chargingRate) { this.chargingRate = chargingRate; }
}
