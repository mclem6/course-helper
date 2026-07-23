package com.coursehelper.backend.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {

    private String message;
    private String conversationId;

    public ChatRequestDto(){}

}
