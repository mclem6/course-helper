package com.coursehelper.backend.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coursehelper.backend.event.dto.EventResponseDto;

@Service
public class EventService {

    private final EventRepository eventRepository;


    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }


    public Event addEvent(Long userId, Long courseId, String title, String eventType, Long assignmentId, LocalDate startDate, String startTime, String endTime, String recurringDays, Boolean isRecurring){
        Event event = new Event(userId, courseId,title, eventType, assignmentId, startDate, startTime, endTime, recurringDays, isRecurring);
        return eventRepository.save(event);
    }



    List<EventResponseDto> getCurrentUserEventsByCourse(Long userId, Long courseId){
        List<Event> events = eventRepository.findByCourseIdAndUserId(courseId, userId);
        return events.stream().map(this::toResponse).toList();
        
    }

    Map<Long, List<EventResponseDto>> getCurrentUserEventsAllCourses(Long userId){
        List<Event> events = eventRepository.findByUserId(userId);
        return events.stream()
        .map(this::toResponse)
        .collect(Collectors.groupingBy(EventResponseDto::getCourseId));
    }


    public EventResponseDto toResponse(Event event){
        return new EventResponseDto(event.getId(), event.getCourseId(), event.getTitle(), event.getEventType(), event.getAssignmentId(),event.getStartDate(), 
        event.getStartTime(), event.getEndTime(), event.getIsRecurring(), event.getRecurringDays());
    }




}
