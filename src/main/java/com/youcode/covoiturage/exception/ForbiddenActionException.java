package com.youcode.covoiturage.exception;

public class ForbiddenActionException extends RuntimeException{
    public ForbiddenActionException(String message) {
        super(message);
    }
}
