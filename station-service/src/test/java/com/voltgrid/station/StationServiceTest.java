package com.voltgrid.station;

import com.voltgrid.station.dto.StationDto;
import com.voltgrid.station.entity.Station;
import com.voltgrid.station.entity.StationStatus;
import com.voltgrid.station.exception.ResourceNotFoundException;
import com.voltgrid.station.exception.StationUnavailableException;
import com.voltgrid.station.repository.StationRepository;
import com.voltgrid.station.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository repository;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(repository);
    }

    @Test
    void testCreateStation() {
        StationDto dto = new StationDto(null, "VoltCentral-1", "Downtown Hub", "CCS2", 150.0, StationStatus.AVAILABLE, 2, 2, 0.0);
        Station saved = new Station("VoltCentral-1", "Downtown Hub", "CCS2", 150.0, StationStatus.AVAILABLE, 2);
        saved.setId(1L);

        when(repository.save(any(Station.class))).thenReturn(saved);

        StationDto result = stationService.createStation(dto);
        assertNotNull(result);
        assertEquals("VoltCentral-1", result.getStationName());
        assertEquals(2, result.getAvailableConnectors());
    }

    @Test
    void testReserveConnectorOccupiedLifecycle() {
        Station station = new Station("VoltCentral-1", "Downtown Hub", "CCS2", 150.0, StationStatus.AVAILABLE, 1);
        station.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(station));
        when(repository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StationDto reserved = stationService.reserveConnector(1L, 50.0);
        assertEquals(0, reserved.getAvailableConnectors());
        assertEquals(StationStatus.OCCUPIED, reserved.getStatus());
        assertEquals(50.0, reserved.getGridLoad());
    }

    @Test
    void testReserveConnectorFailsWhenNoConnectors() {
        Station station = new Station("VoltCentral-1", "Downtown Hub", "CCS2", 150.0, StationStatus.OCCUPIED, 1);
        station.setId(1L);
        station.setAvailableConnectors(0);
        station.setStatus(StationStatus.OCCUPIED);
        when(repository.findById(1L)).thenReturn(Optional.of(station));

        assertThrows(StationUnavailableException.class, () -> stationService.reserveConnector(1L, 50.0));
    }

    @Test
    void testGridLoadBalancingExceedsCapacityThrowsException() {
        Station station = new Station("VoltCentral-1", "Downtown Hub", "CCS2", 100.0, StationStatus.AVAILABLE, 2);
        station.setId(1L);
        station.setGridLoad(80.0);
        when(repository.findById(1L)).thenReturn(Optional.of(station));

        // Requesting 50 kW when capacity is 100 kW and current load is 80 kW (80 + 50 = 130 > 100)
        assertThrows(StationUnavailableException.class, () -> stationService.reserveConnector(1L, 50.0));
    }

    @Test
    void testReleaseConnectorRestoresAvailableStatus() {
        Station station = new Station("VoltCentral-1", "Downtown Hub", "CCS2", 150.0, StationStatus.OCCUPIED, 1);
        station.setId(1L);
        station.setAvailableConnectors(0);
        station.setGridLoad(50.0);

        when(repository.findById(1L)).thenReturn(Optional.of(station));
        when(repository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StationDto released = stationService.releaseConnector(1L, 50.0);
        assertEquals(1, released.getAvailableConnectors());
        assertEquals(StationStatus.AVAILABLE, released.getStatus());
        assertEquals(0.0, released.getGridLoad());
    }

    @Test
    void testStationNotFoundThrows404() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> stationService.getStationById(999L));
    }
}
