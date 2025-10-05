package com.roos.adoptioncenter.adoptionpaper_service.utils.ExceptionsHandling;

public class DogServiceException extends RuntimeException {
    public DogServiceException(String message) {
        super(message);
    }
}