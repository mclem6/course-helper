package com.coursehelper.frontend.exceptions;

public class CourseApiException extends RuntimeException {

    public CourseApiException(String message,Throwable cause){
        super(message, cause);
    }    
}
