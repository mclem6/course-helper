package com.coursehelper.backend.task;

import java.time.LocalDate;

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
@Table(name = "tasks")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long userId;
    Long courseId;
    String title;
    LocalDate dueDate;
    Boolean completed;
    
    public Task(Long userId, Long courseId, String title, LocalDate dueDate,Boolean completed){
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public Task(Long userId, Long courseId, String title, Boolean completed){
        this.userId = userId;
        this.courseId = courseId;
        this.title = title;
        this.completed = completed;
    }
    
}
