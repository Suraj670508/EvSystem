package com.voltgrid.session.service;

import com.voltgrid.session.client.BillingClient;
import com.voltgrid.session.client.StationClient;
import com.voltgrid.session.client.dto.CreateBillingRequest;
import com.voltgrid.session.client.dto.ReservationRequest;
import com.voltgrid.session.client.dto.UpdateEnergyBillingRequest;
import com.voltgrid.session.dto.CreateSessionRequest;
import com.voltgrid.session.dto.SessionDto;
import com.voltgrid.session.entity.ChargingSession;
import com.voltgrid.session.entity.SessionStatus;
import com.voltgrid.session.event.ChargingCompletedEvent;
import com.voltgrid.session.event.ChargingEnergyUpdatedEvent;
import com.voltgrid.session.event.ChargingStartedEvent;
import com.voltgrid.session.exception.ResourceNotFoundException;
import com.voltgrid.session.exception.SessionNotActiveException;
import com.voltgrid.session.repository.ChargingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final ChargingSessionRepository repository;
    private final StationClient stationClient;
    private final BillingClient billingClient;
    private final ApplicationEventPublisher eventPublisher;

    public SessionService(
            ChargingSessionRepository repository,
            StationClient stationClient,
            BillingClient billingClient,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.stationClient = stationClient;
        this.billingClient = billingClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SessionDto createSession(CreateSessionRequest request) {
        if (request.getUserId() == null || request.getStationId() == null) {
            throw new IllegalArgumentException("User ID and Station ID cannot be null");
        }

        double rate = request.getChargingRate();

        // Step 1: Inter-service call to Station Service to reserve connector & verify grid headroom
        stationClient.reserveConnector(request.getStationId(), new ReservationRequest(rate));

        // Step 2: Create Active Charging Session
        ChargingSession session = new ChargingSession(request.getUserId(), request.getStationId(), rate);
        ChargingSession saved = repository.save(session);

        // Step 3: Inter-service call to Billing Service to initialize pending invoice
        try {
            billingClient.createBilling(new CreateBillingRequest(saved.getId(), saved.getUserId(), 0.0, null));
        } catch (Exception e) {
            log.warn("Failed to create billing record synchronously for session {}: {}", saved.getId(), e.getMessage());
        }

        // Step 4: Publish asynchronous event
        eventPublisher.publishEvent(new ChargingStartedEvent(saved.getId(), saved.getUserId(), saved.getStationId()));

        return mapToDto(saved);
    }

    public List<SessionDto> getAllSessions() {
        return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public SessionDto getSessionById(Long id) {
        ChargingSession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found with id: " + id));
        return mapToDto(session);
    }

    @Transactional
    public SessionDto updateEnergy(Long id, Double energyConsumed) {
        if (energyConsumed == null || energyConsumed < 0) {
            throw new IllegalArgumentException("Energy consumed must be greater than or equal to 0");
        }

        ChargingSession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found with id: " + id));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new SessionNotActiveException("Cannot update energy: session " + id + " is in state " + session.getStatus());
        }

        if (energyConsumed < session.getEnergyConsumed()) {
            throw new IllegalArgumentException("New energy consumed cannot be less than previous reading (" + session.getEnergyConsumed() + " kWh)");
        }

        session.setEnergyConsumed(energyConsumed);
        ChargingSession saved = repository.save(session);

        // Propagate updated consumption to Billing Service
        try {
            billingClient.updateEnergy(session.getId(), new UpdateEnergyBillingRequest(energyConsumed));
        } catch (Exception e) {
            log.warn("Failed to update billing energy for session {}: {}", session.getId(), e.getMessage());
        }

        // Asynchronous event notification
        eventPublisher.publishEvent(new ChargingEnergyUpdatedEvent(session.getId(), energyConsumed));

        return mapToDto(saved);
    }

    @Transactional
    public SessionDto completeSession(Long id) {
        ChargingSession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found with id: " + id));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new SessionNotActiveException("Cannot complete session: session " + id + " is in state " + session.getStatus());
        }

        session.setStatus(SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        ChargingSession saved = repository.save(session);

        // Step 1: Release connector on Station Service
        try {
            stationClient.releaseConnector(session.getStationId(), new ReservationRequest(session.getChargingRate()));
        } catch (Exception e) {
            log.error("Failed to release station connector for station {}: {}", session.getStationId(), e.getMessage());
        }

        // Step 2: Finalize billing consumption
        try {
            billingClient.updateEnergy(session.getId(), new UpdateEnergyBillingRequest(session.getEnergyConsumed()));
        } catch (Exception e) {
            log.warn("Failed to finalize billing for session {}: {}", session.getId(), e.getMessage());
        }

        // Step 3: Publish event
        eventPublisher.publishEvent(new ChargingCompletedEvent(session.getId(), session.getStationId(), session.getEnergyConsumed()));

        return mapToDto(saved);
    }

    @Transactional
    public void deleteSession(Long id) {
        ChargingSession session = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found with id: " + id));
        repository.delete(session);
    }

    private SessionDto mapToDto(ChargingSession session) {
        return new SessionDto(
                session.getId(),
                session.getUserId(),
                session.getStationId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getEnergyConsumed(),
                session.getStatus(),
                session.getChargingRate()
        );
    }
}
