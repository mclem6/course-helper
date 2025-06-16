package com.coursehelper.controllers;

import java.util.List;

import com.coursehelper.Course;
import com.coursehelper.UserSession;
import com.coursehelper.dao.CourseDAO;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class HomePageController {

    @FXML
    HBox coursesContainer;

    UserSession userSession = UserSession.getInstance();
    CourseDAO courseDAO;
    

    public void initialize(){
        //initizialize course data access object
        CourseDAO.init();
        courseDAO = CourseDAO.getInstance();

        
        //get user's courses
        List<Course> user_courses = courseDAO.getCoursesByUser(userSession.getUserId());

        if(user_courses.isEmpty()){
            Text courseNode = new Text("No Courses");
            coursesContainer.getChildren().add(courseNode);

        } else {
            //add course to UI
            for (Course course : user_courses){
            Text courseNode = new Text(course.getCourseName());
            coursesContainer.getChildren().add(courseNode);
        }


        }
        


    }

    

    
    

}