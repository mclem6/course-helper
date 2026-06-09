package com.coursehelper.backend.userSettings;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    Long userId;
    String currentSemester;
    int currentYear;
    LocalDate semesterStart;
    LocalDate semesterEnd;
    LocalDateTime updatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public UserSettings(Long userId, String currentSemester, int currentYear, LocalDate semesterStart, LocalDate semesterEnd ){
        this.userId = userId;
        this.currentSemester = currentSemester;
        this.currentYear = currentYear;
        this.semesterStart = semesterStart;
        this.semesterEnd = semesterEnd;

    }

    
}
