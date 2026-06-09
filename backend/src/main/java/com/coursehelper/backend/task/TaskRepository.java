package com.coursehelper.backend.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>{

    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndCompleted(Long userId, Boolean completed);
    List<Task> findByCourseIdAndUserId(Long courseId, Long userId);
    
}
