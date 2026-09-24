package com.voltgrid.station.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stationName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String connectorType;

    @Column(nullable = false)
    private Double chargingCapacity; // in kW (e.g. 150.0 kW)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationStatus status;

    @Column(nullable = false)
    private Integer availableConnectors;

    @Column(nullable = false)
    private Integer totalConnectors;

    @Column(nullable = false)
    private Double gridLoad; // Current aggregate load in kW (e.g. 50.0)

    @Version
    private Long version; // Optimistic locking guard for concurrency

    public Station() {}

    public Station(String stationName, String location, String connectorType, Double chargingCapacity,
                   StationStatus status, Integer totalConnectors) {
        this.stationName = stationName;
        this.location = location;
        this.connectorType = connectorType;
        this.chargingCapacity = chargingCapacity != null ? chargingCapacity : 100.0;
        this.status = status != null ? status : StationStatus.AVAILABLE;
        this.totalConnectors = totalConnectors != null ? totalConnectors : 1;
        this.availableConnectors = this.totalConnectors;
        this.gridLoad = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public Double getChargingCapacity() {
        return chargingCapacity;
    }

    public void setChargingCapacity(Double chargingCapacity) {
        this.chargingCapacity = chargingCapacity;
    }

    public StationStatus getStatus() {
        return status;
    }

    public void setStatus(StationStatus status) {
        this.status = status;
    }

    public Integer getAvailableConnectors() {
        return availableConnectors;
    }

    public void setAvailableConnectors(Integer availableConnectors) {
        this.availableConnectors = availableConnectors;
    }

    public Integer getTotalConnectors() {
        return totalConnectors;
    }

    public void setTotalConnectors(Integer totalConnectors) {
        this.totalConnectors = totalConnectors;
    }

    public Double getGridLoad() {
        return gridLoad;
    }

    public void setGridLoad(Double gridLoad) {
        this.gridLoad = gridLoad;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
