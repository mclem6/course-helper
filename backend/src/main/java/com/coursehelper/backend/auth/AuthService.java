package com.coursehelper.backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coursehelper.backend.auth.dto.LoginRequest;
import com.coursehelper.backend.auth.util.JwtUtil;
import com.coursehelper.backend.exceptions.InvalidCredentialsException;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.user.User;
import com.coursehelper.backend.user.UserRepository;

@Service 
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }


    public User validateCredentials(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if(encoder.matches(request.getPassword(), user.getPassword())){
            return user;
        }
        else {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
    }

    public String generateToken(User user){
        return jwtUtil.generateToken(user);
    }

    
}
