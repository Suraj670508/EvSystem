package com.voltgrid.session.controller;

import com.voltgrid.session.dto.CreateSessionRequest;
import com.voltgrid.session.dto.EnergyUpdateRequest;
import com.voltgrid.session.dto.SessionDto;
import com.voltgrid.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(@RequestBody CreateSessionRequest request) {
        SessionDto created = service.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        return ResponseEntity.ok(service.getAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getSessionById(id));
    }

    @PutMapping("/{id}/energy")
    public ResponseEntity<SessionDto> updateEnergy(
            @PathVariable("id") Long id,
            @RequestBody EnergyUpdateRequest request) {
        return ResponseEntity.ok(service.updateEnergy(id, request.getEnergyConsumed()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<SessionDto> completeSession(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.completeSession(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable("id") Long id) {
        service.deleteSession(id);
        return ResponseEntity.ok(Map.of("message", "Charging session deleted successfully"));
    }
}
