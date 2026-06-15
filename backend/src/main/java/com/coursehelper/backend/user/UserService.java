package com.coursehelper.backend.user;

import java.io.IOException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.coursehelper.backend.auth.util.JwtUtil;
import com.coursehelper.backend.exceptions.FileProcessingException;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.exceptions.UsernameAlreadyExistsException;
import com.coursehelper.backend.user.dto.RegisterRequest;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwUtil){

        this.userRepository = userRepository;
        this.jwtUtil = jwUtil;
        this.encoder = encoder;

    }

    public User createUser(RegisterRequest request){

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException("Username already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.changePassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        return user;

    }

    public String generateToken(User user){   

        return jwtUtil.generateToken(user);
        
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public User findByUserId(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public void uploadProfilePicture(Long userId, MultipartFile file) {
        User user = findByUserId(userId);
        try {
            user.setProfilePicture(file.getBytes());
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read profile picture", e);
        }
        userRepository.save(user);
    }

    public byte[] getProfilePicture(Long userId) {
        return findByUserId(userId).getProfilePicture();
    }

}
