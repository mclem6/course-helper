package com.coursehelper.backend.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coursehelper.backend.auth.util.JwtUtil;
import com.coursehelper.backend.exceptions.InvalidCredentialsException;
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

    public User findByUsername(String username){

        User user = userRepository.findByUsername(username) .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user;
    }

    public User findByUserId(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void changeUsername(Long userId, String newUsername) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already taken.");
        }
        User user = findByUserId(userId);
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findByUserId(userId);
        if (!encoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }
        user.changePassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

}
