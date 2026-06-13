package com.coursehelper.backend.user;


import java.util.List;

import com.coursehelper.backend.course.Course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//model class for holding user data

@Entity 
@Getter 
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.NONE)
    private Long id;

    @Setter(AccessLevel.NONE)
    private String password;

    @Column(unique =true)
    private String username;

    private String theme;

    @OneToMany(mappedBy = "user")
    private List<Course> courses;

    @Column(name = "profile_picture")
    private byte[] profilePicture;
    

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
}


}
