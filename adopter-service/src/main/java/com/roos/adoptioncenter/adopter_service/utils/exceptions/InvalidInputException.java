package com.roos.adoptioncenter.adopter_service.utils.exceptions;
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String msg){
        super(msg);
    }
    public InvalidInputException(Throwable cause){
        super(cause);
    }
    public InvalidInputException(String msg, Throwable cause){
        super(msg, cause);
    }
}