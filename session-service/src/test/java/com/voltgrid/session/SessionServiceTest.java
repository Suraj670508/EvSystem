package com.voltgrid.session;

import com.voltgrid.session.client.BillingClient;
import com.voltgrid.session.client.StationClient;
import com.voltgrid.session.client.dto.ReservationRequest;
import com.voltgrid.session.dto.CreateSessionRequest;
import com.voltgrid.session.dto.SessionDto;
import com.voltgrid.session.entity.ChargingSession;
import com.voltgrid.session.entity.SessionStatus;
import com.voltgrid.session.exception.ResourceNotFoundException;
import com.voltgrid.session.exception.SessionNotActiveException;
import com.voltgrid.session.repository.ChargingSessionRepository;
import com.voltgrid.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private ChargingSessionRepository repository;

    @Mock
    private StationClient stationClient;

    @Mock
    private BillingClient billingClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(repository, stationClient, billingClient, eventPublisher);
    }

    @Test
    void testCreateSessionSuccessfully() {
        CreateSessionRequest request = new CreateSessionRequest(1L, 10L, 50.0);
        ChargingSession saved = new ChargingSession(1L, 10L, 50.0);
        saved.setId(100L);

        when(repository.save(any(ChargingSession.class))).thenReturn(saved);

        SessionDto result = sessionService.createSession(request);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(SessionStatus.ACTIVE, result.getStatus());
        verify(stationClient, times(1)).reserveConnector(eq(10L), any(ReservationRequest.class));
        verify(repository, times(1)).save(any(ChargingSession.class));
    }

    @Test
    void testUpdateEnergySuccessfully() {
        ChargingSession session = new ChargingSession(1L, 10L, 50.0);
        session.setId(100L);
        session.setEnergyConsumed(5.0);

        when(repository.findById(100L)).thenReturn(Optional.of(session));
        when(repository.save(any(ChargingSession.class))).thenAnswer(i -> i.getArgument(0));

        SessionDto updated = sessionService.updateEnergy(100L, 15.0);
        assertEquals(15.0, updated.getEnergyConsumed());
        verify(billingClient, times(1)).updateEnergy(eq(100L), any());
    }

    @Test
    void testUpdateEnergyOnCompletedSessionThrowsException() {
        ChargingSession session = new ChargingSession(1L, 10L, 50.0);
        session.setId(100L);
        session.setStatus(SessionStatus.COMPLETED);

        when(repository.findById(100L)).thenReturn(Optional.of(session));

        assertThrows(SessionNotActiveException.class, () -> sessionService.updateEnergy(100L, 20.0));
    }

    @Test
    void testCompleteSessionReleasesConnector() {
        ChargingSession session = new ChargingSession(1L, 10L, 50.0);
        session.setId(100L);
        session.setEnergyConsumed(25.0);

        when(repository.findById(100L)).thenReturn(Optional.of(session));
        when(repository.save(any(ChargingSession.class))).thenAnswer(i -> i.getArgument(0));

        SessionDto completed = sessionService.completeSession(100L);

        assertEquals(SessionStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getEndTime());
        verify(stationClient, times(1)).releaseConnector(eq(10L), any(ReservationRequest.class));
    }

    @Test
    void testSessionNotFoundThrows404() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> sessionService.getSessionById(999L));
    }
}
