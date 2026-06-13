package com.coursehelper.backend.course;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.coursehelper.backend.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//model class for holding user data

@Entity 
@Getter 
@Setter
@NoArgsConstructor
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String style;
    private String semester;
    private Integer courseYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String startTime;
    private String endTime;
    private Boolean recurring;
    private String lectureDays;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public Course(String name, String style, User user, String semester, Integer courseYear,
    LocalDate startDate, LocalDate endDate, String startTime, String endTime,
    Boolean recurring, String lectureDays) {
        this.name = name;
        this.style = style;
        this.user = user;
        this.semester = semester;
        this.courseYear = courseYear;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurring = recurring;
        this.lectureDays = lectureDays;
    }
    

    
}
