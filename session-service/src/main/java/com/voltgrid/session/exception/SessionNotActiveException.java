package com.voltgrid.session.exception;

public class SessionNotActiveException extends RuntimeException {
    public SessionNotActiveException(String message) {
        super(message);
    }
}
