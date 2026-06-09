package com.coursehelper.backend.exceptions;

public class DuplicateFileException extends RuntimeException{

    public DuplicateFileException(String message){
        super(message);
    }

}
