package com.coursehelper.backend.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long courseId;
    private String title;
    private String eventType;
    private LocalDate startDate;
    private String startTime; 
    private String endTime;
    private Boolean isRecurring;
    private String recurringDays;
    private Long assignmentId; 
   
    

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Event(Long userId, Long courseId, String title, String eventType, Long assignmentId, LocalDate startDate, String startTime, String endTime, String recurringDays, Boolean isRecurring){
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.eventType = eventType;
        this.assignmentId = assignmentId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRecurring = isRecurring; 
        this.recurringDays = recurringDays;

    }


    
}
