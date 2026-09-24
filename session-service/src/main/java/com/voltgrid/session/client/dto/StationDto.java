package com.voltgrid.session.client.dto;

public class StationDto {
    private Long id;
    private String stationName;
    private String location;
    private String connectorType;
    private Double chargingCapacity;
    private String status;
    private Integer availableConnectors;
    private Integer totalConnectors;
    private Double gridLoad;

    public StationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
    public Double getChargingCapacity() { return chargingCapacity; }
    public void setChargingCapacity(Double chargingCapacity) { this.chargingCapacity = chargingCapacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAvailableConnectors() { return availableConnectors; }
    public void setAvailableConnectors(Integer availableConnectors) { this.availableConnectors = availableConnectors; }
    public Integer getTotalConnectors() { return totalConnectors; }
    public void setTotalConnectors(Integer totalConnectors) { this.totalConnectors = totalConnectors; }
    public Double getGridLoad() { return gridLoad; }
    public void setGridLoad(Double gridLoad) { this.gridLoad = gridLoad; }
}
