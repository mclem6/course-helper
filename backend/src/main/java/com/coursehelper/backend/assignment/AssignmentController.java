package com.coursehelper.backend.assignment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.assignment.dto.AddAssignmentRequestDto;
import com.coursehelper.backend.assignment.dto.AssignmentCreatedResponseDto;
import com.coursehelper.backend.assignment.dto.AssignmentResponseDto;
import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.event.Event;
import com.coursehelper.backend.event.EventService;
import com.coursehelper.backend.user.UserService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    AssignmentService assignmentService;
    UserService userService;
    EventService eventService;

    public AssignmentController(AssignmentService assignmentService, UserService userService, EventService eventService){
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Transactional
    @PostMapping("/assignment")
    public ResponseEntity<AssignmentCreatedResponseDto> addAssignment(@RequestBody AddAssignmentRequestDto request, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();

        //create assignment
        Assignment assignment = assignmentService.addAssignment(userId,request.getCourseId(), request.getTitle(), request.getDescription(), 
        request.getDueDate(), request.getDueTime(),request.getStatus(), request.getAssignmentType());

        //create event 
        Event event = eventService.addEvent(userId, request.getCourseId(), request.getTitle(), request.getAssignmentType(), assignment.getId(),
        request.getDueDate(), request.getDueTime(), request.getDueTime(), null, false);

        return ResponseEntity.ok(assignmentService.toResponse(assignment, event));
        
    }
    
    @PatchMapping("/assignment/{id}/complete")
    public ResponseEntity<AssignmentResponseDto> markComplete(@PathVariable Long id, Authentication auth) {
        CustomUserPrincipal user = (CustomUserPrincipal) auth.getPrincipal();
        return ResponseEntity.ok(assignmentService.markComplete(id, user.getUserId()));
    }

    @GetMapping("/assignments")
    public ResponseEntity<?> getAssignments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status,
            Authentication auth) {

        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();

        if (courseId != null) {
            return ResponseEntity.ok(
                assignmentService.getUserAssignmentsByCourseId(userId, courseId, status)
            );
        } else {
            return ResponseEntity.ok(
                assignmentService.getUserAssignmentsAllCourses(userId, status)
            );
        }


    }





    
}
