package com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
