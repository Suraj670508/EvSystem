package com.voltgrid.station.controller;

import com.voltgrid.station.dto.ReservationRequest;
import com.voltgrid.station.dto.StationDto;
import com.voltgrid.station.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService service;

    public StationController(StationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StationDto> createStation(@RequestBody StationDto dto) {
        StationDto created = service.createStation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<StationDto>> getAllStations() {
        return ResponseEntity.ok(service.getAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDto> getStationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getStationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationDto> updateStation(@PathVariable("id") Long id, @RequestBody StationDto dto) {
        return ResponseEntity.ok(service.updateStation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStation(@PathVariable("id") Long id) {
        service.deleteStation(id);
        return ResponseEntity.ok(Map.of("message", "Station deleted successfully"));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<StationDto> reserveConnector(
            @PathVariable("id") Long id,
            @RequestBody(required = false) ReservationRequest request) {
        Double rate = (request != null) ? request.getChargingRate() : 50.0;
        return ResponseEntity.ok(service.reserveConnector(id, rate));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<StationDto> releaseConnector(
            @PathVariable("id") Long id,
            @RequestBody(required = false) ReservationRequest request) {
        Double rate = (request != null) ? request.getChargingRate() : 50.0;
        return ResponseEntity.ok(service.releaseConnector(id, rate));
    }
}
