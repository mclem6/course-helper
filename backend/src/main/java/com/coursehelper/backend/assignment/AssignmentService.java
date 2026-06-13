package com.coursehelper.backend.assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coursehelper.backend.assignment.dto.AssignmentCreatedResponseDto;
import com.coursehelper.backend.assignment.dto.AssignmentResponseDto;
import com.coursehelper.backend.course.CourseRepository;
import com.coursehelper.backend.event.Event;
import com.coursehelper.backend.event.dto.EventResponseDto;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.userSettings.SettingsRepository;
import com.coursehelper.backend.userSettings.UserSettings;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             CourseRepository courseRepository,
                             SettingsRepository settingsRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.settingsRepository = settingsRepository;
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

    public List<AssignmentResponseDto> getUserAssignmentsByCourseId(Long userId, Long courseId, String status){
        List<Assignment> assignments = status != null
            ? assignmentRepository.findByCourseIdAndUserIdAndStatus(courseId, userId, status)
            : assignmentRepository.findByCourseIdAndUserId(courseId, userId);
        return assignments.stream().map(this::toResponse).toList();
    }

    public Map<Long, List<AssignmentResponseDto>> getUserAssignmentsAllCourses(Long userId, String status){
        UserSettings settings = settingsRepository.findByUserId(userId).orElse(null);

        List<Assignment> assignments;
        if (settings != null) {
            List<Long> courseIds = courseRepository
                .findByUserIdAndSemesterAndCourseYear(userId, settings.getCurrentSemester(), settings.getCurrentYear())
                .stream().map(c -> c.getId()).collect(Collectors.toList());
            if (courseIds.isEmpty()) return Map.of();
            assignments = status != null
                ? assignmentRepository.findByUserIdAndCourseIdInAndStatus(userId, courseIds, status)
                : assignmentRepository.findByUserIdAndCourseIdIn(userId, courseIds);
        } else {
            assignments = status != null
                ? assignmentRepository.findByUserIdAndStatus(userId, status)
                : assignmentRepository.findByUserId(userId);
        }

        return assignments.stream()
            .map(this::toResponse)
            .collect(Collectors.groupingBy(AssignmentResponseDto::getCourseId));
    }

    public AssignmentResponseDto markComplete(Long assignmentId, Long userId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .filter(a -> a.getUserId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assignment.setStatus("COMPLETED");
        return toResponse(assignmentRepository.save(assignment));
    }



}
