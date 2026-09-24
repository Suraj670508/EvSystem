package com.voltgrid.station.service;

import com.voltgrid.station.dto.StationDto;
import com.voltgrid.station.entity.Station;
import com.voltgrid.station.entity.StationStatus;
import com.voltgrid.station.exception.ResourceNotFoundException;
import com.voltgrid.station.exception.StationUnavailableException;
import com.voltgrid.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationRepository repository;

    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public StationDto createStation(StationDto dto) {
        if (dto.getStationName() == null || dto.getStationName().trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be empty");
        }
        int total = dto.getTotalConnectors() != null ? dto.getTotalConnectors() : 1;
        Double capacity = dto.getChargingCapacity() != null ? dto.getChargingCapacity() : 150.0;
        StationStatus status = dto.getStatus() != null ? dto.getStatus() : StationStatus.AVAILABLE;

        Station station = new Station(
                dto.getStationName(),
                dto.getLocation() != null ? dto.getLocation() : "Unknown",
                dto.getConnectorType() != null ? dto.getConnectorType() : "CCS2",
                capacity,
                status,
                total
        );
        Station saved = repository.save(station);
        return mapToDto(saved);
    }

    public List<StationDto> getAllStations() {
        return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public StationDto getStationById(Long id) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
        return mapToDto(station);
    }

    @Transactional
    public StationDto updateStation(Long id, StationDto dto) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        if (dto.getStationName() != null) station.setStationName(dto.getStationName());
        if (dto.getLocation() != null) station.setLocation(dto.getLocation());
        if (dto.getConnectorType() != null) station.setConnectorType(dto.getConnectorType());
        if (dto.getChargingCapacity() != null) station.setChargingCapacity(dto.getChargingCapacity());
        if (dto.getStatus() != null) station.setStatus(dto.getStatus());
        if (dto.getAvailableConnectors() != null) station.setAvailableConnectors(dto.getAvailableConnectors());
        if (dto.getTotalConnectors() != null) station.setTotalConnectors(dto.getTotalConnectors());
        if (dto.getGridLoad() != null) station.setGridLoad(dto.getGridLoad());

        Station updated = repository.save(station);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteStation(Long id) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
        repository.delete(station);
    }

    @Transactional
    public StationDto reserveConnector(Long id, Double requestedRate) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        if (station.getStatus() == StationStatus.MAINTENANCE || station.getStatus() == StationStatus.OFFLINE) {
            throw new StationUnavailableException("Station " + id + " is currently " + station.getStatus());
        }

        if (station.getAvailableConnectors() <= 0) {
            throw new StationUnavailableException("Station " + id + " has no available connectors (Status: OCCUPIED)");
        }

        double rate = requestedRate != null && requestedRate > 0 ? requestedRate : 50.0;

        // Grid Load Balancing Logic: verify aggregate grid load does not breach capacity
        double projectedLoad = station.getGridLoad() + rate;
        if (projectedLoad > station.getChargingCapacity()) {
            throw new StationUnavailableException(String.format(
                    "Grid load capacity exceeded at Station %d. Capacity: %.1f kW, Current Load: %.1f kW, Requested: %.1f kW",
                    id, station.getChargingCapacity(), station.getGridLoad(), rate
            ));
        }

        // Deduct available connector and update grid load
        station.setAvailableConnectors(station.getAvailableConnectors() - 1);
        station.setGridLoad(projectedLoad);

        if (station.getAvailableConnectors() == 0) {
            station.setStatus(StationStatus.OCCUPIED);
        }

        Station saved = repository.save(station);
        return mapToDto(saved);
    }

    @Transactional
    public StationDto releaseConnector(Long id, Double releasedRate) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));

        double rate = releasedRate != null && releasedRate > 0 ? releasedRate : 50.0;

        if (station.getAvailableConnectors() < station.getTotalConnectors()) {
            station.setAvailableConnectors(station.getAvailableConnectors() + 1);
        }

        double updatedLoad = Math.max(0.0, station.getGridLoad() - rate);
        station.setGridLoad(updatedLoad);

        if (station.getAvailableConnectors() > 0 && station.getStatus() == StationStatus.OCCUPIED) {
            station.setStatus(StationStatus.AVAILABLE);
        }

        Station saved = repository.save(station);
        return mapToDto(saved);
    }

    private StationDto mapToDto(Station station) {
        return new StationDto(
                station.getId(),
                station.getStationName(),
                station.getLocation(),
                station.getConnectorType(),
                station.getChargingCapacity(),
                station.getStatus(),
                station.getAvailableConnectors(),
                station.getTotalConnectors(),
                station.getGridLoad()
        );
    }
}
