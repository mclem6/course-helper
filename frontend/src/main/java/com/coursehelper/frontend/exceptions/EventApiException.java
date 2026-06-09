package com.coursehelper.frontend.exceptions;

public class EventApiException extends RuntimeException {

    public EventApiException(String message,Throwable cause){
        super(message, cause);
    }    

}