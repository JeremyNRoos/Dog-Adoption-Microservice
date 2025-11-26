package com.roos.adoptioncenter.apigateway.ExceptionsHandling;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
