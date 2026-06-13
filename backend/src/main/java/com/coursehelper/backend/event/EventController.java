package com.coursehelper.backend.event;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.event.dto.AddEventRequestDto;
import com.coursehelper.backend.event.dto.EventResponseDto;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @PostMapping("/event")
    public ResponseEntity<EventResponseDto> addEvent(@RequestBody AddEventRequestDto request, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();

        Event event = eventService.addEvent(userId, request.getCourseId(), request.getTitle(), request.getEventType(), request.getAssignmentId(), request.getStartDate(),
                        request.getStartTime(), request.getEndTime(), request.getRecurringDays(), request.getIsRecurring());

        return ResponseEntity.ok(eventService.toResponse(event));

    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(@RequestParam(required = false) Long courseId, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();

    
        if (courseId != null) {
            return ResponseEntity.ok(
                eventService.getCurrentUserEventsByCourse(userId, courseId)
            );

        } else {
            return ResponseEntity.ok(
                eventService.getCurrentUserEventsAllCourses(userId)
            );
        }


    }

    








    
    
}
