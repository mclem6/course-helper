package com.coursehelper.frontend.dto;

public class ChatRequestDto {

    String message;

    public ChatRequestDto(){}

    public ChatRequestDto(String message){
        this.message = message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
    
}
