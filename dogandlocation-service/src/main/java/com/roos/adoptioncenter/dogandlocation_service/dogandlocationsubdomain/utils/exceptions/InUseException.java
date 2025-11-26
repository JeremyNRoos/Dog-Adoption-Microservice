package com.roos.adoptioncenter.dogandlocation_service.dogandlocationsubdomain.utils.exceptions;

public class InUseException extends RuntimeException {
    public InUseException(String message) {
        super(message);
    }

    public InUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public InUseException(Throwable cause) {
        super(cause);
    }
}