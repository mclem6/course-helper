package com.coursehelper.frontend.dto;

public class AssignmentCreatedResponseDto {

    private AssignmentResponseDto assignment;
    private EventResponseDto event;

    public AssignmentCreatedResponseDto() {}

    public AssignmentResponseDto getAssignment() { return assignment; }
    public EventResponseDto getEvent() { return event; }

    public void setAssignment(AssignmentResponseDto assignment) { this.assignment = assignment; }
    public void setEvent(EventResponseDto event) { this.event = event; }
    
}