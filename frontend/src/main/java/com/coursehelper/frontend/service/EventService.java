package com.coursehelper.frontend.service;

import java.io.IOException;
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
        } catch (IOException e) {
            throw new ApiException("Unable to create event. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public List<Event> getCourseEvents(Long courseId) {
        try {
            List<EventResponseDto> eventResponseList = apiClient.get(
                "/events?courseId=" + courseId,
                new TypeReference<List<EventResponseDto>>() {});

            return eventResponseList.stream()
                .map(dto -> new Event(dto.getId(), dto.getUserId(), dto.getCourseId(),
                    dto.getTitle(), dto.getEventType(), dto.getAssignmentId(),
                    dto.getStartDate(), dto.getStartTime(), dto.getEndTime(),
                    dto.getIsRecurring(), dto.getRecurringDays()))
                .toList();

        } catch (IOException e) {
            throw new ApiException("Unable to fetch events. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public Map<Long, List<Event>> getAllEvents() {
        try {
            Map<Long, List<EventResponseDto>> eventResponseList = apiClient.get(
                "/events",
                new TypeReference<Map<Long, List<EventResponseDto>>>() {});

            return eventResponseList.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                        .map(dto -> new Event(dto.getId(), dto.getUserId(), dto.getCourseId(),
                            dto.getTitle(), dto.getEventType(), dto.getAssignmentId(),
                            dto.getStartDate(), dto.getStartTime(), dto.getEndTime(),
                            dto.getIsRecurring(), dto.getRecurringDays()))
                        .collect(Collectors.toList())
                ));

        } catch (IOException e) {
            throw new ApiException("Unable to fetch events. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }


}