package com.voltgrid.station.exception;

public class StationUnavailableException extends RuntimeException {
    public StationUnavailableException(String message) {
        super(message);
    }
}
