package com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
