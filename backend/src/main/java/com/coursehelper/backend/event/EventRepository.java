package com.coursehelper.backend.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCourseIdAndUserId(Long courseId, Long userId);

    List<Event> findByUserId(Long userId);
    
    List<Event> findByUserIdAndEventType(Long userId, String eventType);
    


}
