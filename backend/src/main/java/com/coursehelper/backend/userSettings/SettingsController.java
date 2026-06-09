package com.coursehelper.backend.userSettings;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.user.UserService;
import com.coursehelper.backend.userSettings.dto.SettingsRequestDto;
import com.coursehelper.backend.userSettings.dto.SettingsResponseDto;

@RestController
@RequestMapping("/api")
public class SettingsController {

    private final UserService userService;
    private final SettingsRepository settingsRepository;

    public SettingsController(UserService userService, SettingsRepository settingsRepository){
        this.userService = userService;
        this.settingsRepository = settingsRepository;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponseDto> getSettings(Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();

        return settingsRepository.findByUserId(userId)
        .map(s -> ResponseEntity.ok(toResponse(s)))
        .orElse(ResponseEntity.noContent().build());
        
    }

    @PutMapping("/settings")
    public ResponseEntity<SettingsResponseDto> saveSettings( @RequestBody SettingsRequestDto dto, Authentication auth) {
        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();

        // find existing or create new
        UserSettings settings = settingsRepository.findByUserId(userId)
            .orElse(new UserSettings()); // ← creates new if not found

        settings.setUserId(userId);
        settings.setCurrentSemester(dto.getSemester());
        settings.setCurrentYear(dto.getYear());
        settings.setSemesterStart(dto.getStartDate());
        settings.setSemesterEnd(dto.getEndDate());
        settings.setUpdatedAt(LocalDateTime.now());

        if (settings.getCreatedAt() == null) {
            settings.setCreatedAt(LocalDateTime.now()); // ← set only on creation
        }

        return ResponseEntity.ok(toResponse(settingsRepository.save(settings)));
    }

    SettingsResponseDto toResponse(UserSettings settings){
        return new SettingsResponseDto(settings.getCurrentSemester(), settings.getCurrentYear(),
                    settings.getSemesterStart(), settings.getSemesterEnd());
    }





    
}
