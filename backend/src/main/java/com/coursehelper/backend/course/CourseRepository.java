package com.coursehelper.backend.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long>{

    List<Course> findAllByUserId(Long userId);
    
    Optional<Course> findByNameAndUserId(String name, Long userId);

    List<Course> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    
    List<Course> findByUserIdAndSemesterAndCourseYear(
       Long userId, String semester, int courseYear);

    @Query(value= "SELECT * FROM courses WHERE user_id = :userId " +
           "AND LOWER(lecture_days) LIKE LOWER(CONCAT('%', :day, '%')) " +
           "AND semester = :semester AND course_year = :courseYear",
           nativeQuery = true)
    List<Course> findByUserIdAndDayAndSemesterAndCourseYear(
        @Param("userId") Long userId,
        @Param("day") String day,
        @Param("semester") String semester,
        @Param("courseYear") int courseYear);
 
}
