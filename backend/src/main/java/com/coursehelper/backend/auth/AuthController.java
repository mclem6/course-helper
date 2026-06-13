package com.coursehelper.backend.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.auth.dto.LoginRequest;
import com.coursehelper.backend.auth.dto.LoginResponseDto;
import com.coursehelper.backend.user.User;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequest request) {
        User user = authService.validateCredentials(request);
        String token = authService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDto(token, user.getId(), user.getUsername(), user.getTheme()));
         
    }
        
    
    
    
}
