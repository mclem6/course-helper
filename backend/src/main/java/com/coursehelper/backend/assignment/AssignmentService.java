package com.coursehelper.backend.assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coursehelper.backend.assignment.dto.AssignmentCreatedResponseDto;
import com.coursehelper.backend.assignment.dto.AssignmentResponseDto;
import com.coursehelper.backend.event.Event;
import com.coursehelper.backend.event.dto.EventResponseDto;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository){
        this.assignmentRepository = assignmentRepository;
    } 


    public Assignment addAssignment(Long userId, Long courseId, String title, String description, LocalDate dueDate, String dueTime, 
    String status, String assignmentType){

        Assignment assignment = new Assignment(userId, courseId, title, description, dueDate, dueTime, status, assignmentType);

        return assignmentRepository.save(assignment);
    }

    public AssignmentResponseDto toResponse(Assignment assignment){

        return new AssignmentResponseDto(assignment.getId(), assignment.getUserId(), assignment.getCourseId(), assignment.getTitle(), 
        assignment.getDescription(), assignment.getDueDate(), assignment.getDueTime(), assignment.getStatus(), assignment.getAssignmentType());

    }

    public AssignmentCreatedResponseDto toResponse(Assignment assignment, Event event){

        AssignmentResponseDto assignmentResponseDto = new AssignmentResponseDto(assignment.getId(), assignment.getUserId(), 
        assignment.getCourseId(), assignment.getTitle(),assignment.getDescription(), assignment.getDueDate(), assignment.getDueTime(), 
        assignment.getStatus(), assignment.getAssignmentType());

        EventResponseDto eventResponseDto = new EventResponseDto(event.getId(), event.getCourseId(), event.getTitle(), 
        event.getEventType(), event.getAssignmentId(),event.getStartDate(), event.getStartTime(), event.getStartTime(), event.getIsRecurring(), 
        event.getRecurringDays());

        return new AssignmentCreatedResponseDto(assignmentResponseDto, eventResponseDto);

    }

    public List<AssignmentResponseDto> getUserAssignmentsByCourseId(Long userId, Long courseId){
        List<Assignment> assignments = assignmentRepository.findByCourseIdAndUserId(courseId, userId);
        return assignments.stream().map(this::toResponse).toList();
        
    }

    public Map<Long, List<AssignmentResponseDto>> getUserAssignmentsAllCourses(Long userId){
        List<Assignment> assignments = assignmentRepository.findByUserId(userId);
        return assignments.stream()
        .map(this::toResponse)
        .collect(Collectors.groupingBy(AssignmentResponseDto::getCourseId));
    }



}
