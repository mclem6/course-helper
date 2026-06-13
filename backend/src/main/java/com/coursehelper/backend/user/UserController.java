package com.coursehelper.backend.user;

import java.io.IOException;

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
import com.coursehelper.backend.exceptions.FileProcessingException;
import com.coursehelper.backend.user.dto.RegisterRequest;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterRequest request){
        User user =  userService.createUser(request);
        String token = userService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDto(token, user.getId(), user.getUsername()));
    }

    @PostMapping("/users/profile-picture")
    public ResponseEntity<String> uploadProfilePicture( @RequestParam("file") MultipartFile file, Authentication auth) {

        CustomUserPrincipal principal = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = principal.getUserId();
        
        User user = userRepository.findById(userId).orElseThrow(() ->
        new RuntimeException("User not found."));

        try {
            byte[] imgBytes = file.getBytes();
            System.out.println("Received bytes: " + imgBytes.length);
            user.setProfilePicture(imgBytes);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read profile picture", e);
        }
        
        userRepository.save(user);

        return ResponseEntity.status(200).body("Profile picture updated");
        
    }

    @GetMapping("/users/profile-picture")
    public ResponseEntity<byte[]> getUserProfilePicture(Authentication auth) {

        CustomUserPrincipal principal = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = principal.getUserId();
        
        User user = userRepository.findById(userId).orElseThrow(() ->
        new RuntimeException("System error, cannot find user."));

        return ResponseEntity.status(200).body(user.getProfilePicture());

    }






}
