package com.coursehelper.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.auth.dto.LoginResponseDto;
import com.coursehelper.backend.user.dto.RegisterRequest;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterRequest request) {
        User user = userService.createUser(request);
        String token = userService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new LoginResponseDto(token, user.getId(), user.getUsername()));
    }

    @PostMapping("/users/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file, Authentication auth) {
        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();
        userService.uploadProfilePicture(userId, file);
        return ResponseEntity.ok("Profile picture updated");
    }

    @GetMapping("/users/profile-picture")
    public ResponseEntity<byte[]> getUserProfilePicture(Authentication auth) {
        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(userService.getProfilePicture(userId));
    }
}
