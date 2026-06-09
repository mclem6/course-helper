package com.coursehelper.backend.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coursehelper.backend.auth.util.JwtUtil;
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

        User user = userRepository.findById(userId) .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user;
    }

    
}
