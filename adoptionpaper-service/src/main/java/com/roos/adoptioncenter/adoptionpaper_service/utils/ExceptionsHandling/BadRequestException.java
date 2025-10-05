package com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

