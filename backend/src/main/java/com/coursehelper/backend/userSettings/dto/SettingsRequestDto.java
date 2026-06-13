package com.coursehelper.backend.userSettings.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SettingsRequestDto {

    String semester;
    int year;
    LocalDate startDate;
    LocalDate endDate;

}
