package com.voltgrid.station.dto;

public class ReservationRequest {
    private Double chargingRate; // Rate in kW (e.g., 50.0 kW)

    public ReservationRequest() {}

    public ReservationRequest(Double chargingRate) {
        this.chargingRate = chargingRate;
    }

    public Double getChargingRate() {
        return chargingRate != null ? chargingRate : 50.0;
    }

    public void setChargingRate(Double chargingRate) {
        this.chargingRate = chargingRate;
    }
}
