package com.coursehelper.backend.userSettings.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SettingsResponseDto {

    String semester;
    Integer year;
    LocalDate startDate;
    LocalDate endDate;

    public SettingsResponseDto(String semester, Integer year, LocalDate startDate, LocalDate endDate){

        this.semester = semester;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
