package com.roos.adoptioncenter.adopter_service.utils.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){
        super(msg);
    }
    public NotFoundException(Throwable cause){
        super(cause);
    }
    public NotFoundException(String msg, Throwable cause){
        super(msg, cause);
    }
}