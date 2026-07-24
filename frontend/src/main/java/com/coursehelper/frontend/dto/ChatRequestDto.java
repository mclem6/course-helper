package com.coursehelper.frontend.dto;

public class ChatRequestDto {

    String message;
    String conversationId;

    public ChatRequestDto(){}

    public ChatRequestDto(String message, String conversationId){
        this.message = message;
        this.conversationId = conversationId;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    
}
