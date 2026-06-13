package com.coursehelper.frontend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.dto.AddEventRequestDto;
import com.coursehelper.frontend.dto.EventResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.model.Event;
import com.fasterxml.jackson.core.type.TypeReference;

public class EventService {

    private static EventService instance;
    private final ApiClient apiClient;

    public EventService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService(new ApiClient());
        }
        return instance;
    }

    public EventResponseDto addEvent(AddEventRequestDto request) {
        try {
            return apiClient.post("/event", request, EventResponseDto.class);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to create event. Check connection.", 503) : e;
        }
    }

    public List<Event> getCourseEvents(Long courseId) {
        try {
            List<EventResponseDto> dtos = apiClient.get(
                "/events?courseId=" + courseId, new TypeReference<List<EventResponseDto>>() {});
            return dtos.stream().map(this::toModel).toList();
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch events. Check connection.", 503) : e;
        }
    }

    public Map<Long, List<Event>> getAllEvents() {
        try {
            Map<Long, List<EventResponseDto>> response = apiClient.get(
                "/events", new TypeReference<Map<Long, List<EventResponseDto>>>() {});
            return response.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().map(this::toModel).collect(Collectors.toList())
                ));
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Unable to fetch events. Check connection.", 503) : e;
        }
    }

    private Event toModel(EventResponseDto dto) {
        return new Event(dto.getId(), dto.getUserId(), dto.getCourseId(),
            dto.getTitle(), dto.getEventType(), dto.getAssignmentId(),
            dto.getStartDate(), dto.getStartTime(), dto.getEndTime(),
            dto.getIsRecurring(), dto.getRecurringDays());
    }
}
