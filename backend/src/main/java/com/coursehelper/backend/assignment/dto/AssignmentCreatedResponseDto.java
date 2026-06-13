package com.coursehelper.backend.assignment.dto;

import lombok.Getter;
import lombok.Setter;

import com.coursehelper.backend.event.dto.EventResponseDto;


@Getter
@Setter
public class AssignmentCreatedResponseDto {
    private AssignmentResponseDto assignment;
    private EventResponseDto event;

    public AssignmentCreatedResponseDto(AssignmentResponseDto assignment, 
                                         EventResponseDto event) {
        this.assignment = assignment;
        this.event = event;
    }

}