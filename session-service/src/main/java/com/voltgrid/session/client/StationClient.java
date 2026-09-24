package com.voltgrid.session.client;

import com.voltgrid.session.client.dto.ReservationRequest;
import com.voltgrid.session.client.dto.StationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "STATION-SERVICE")
public interface StationClient {

    @PostMapping("/stations/{id}/reserve")
    StationDto reserveConnector(@PathVariable("id") Long id, @RequestBody ReservationRequest request);

    @PostMapping("/stations/{id}/release")
    StationDto releaseConnector(@PathVariable("id") Long id, @RequestBody ReservationRequest request);
}
