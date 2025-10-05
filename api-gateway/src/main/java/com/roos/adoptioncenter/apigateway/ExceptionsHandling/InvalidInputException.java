package com.roos.adoptioncenter.apigateway.ExceptionsHandling;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
