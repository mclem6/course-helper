package com.coursehelper.backend.task;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.task.dto.AddTaskRequestDto;
import com.coursehelper.backend.task.dto.TaskResponseDto;
import com.coursehelper.backend.user.User;
import com.coursehelper.backend.user.UserService;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService){
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/task")
    public ResponseEntity<TaskResponseDto> addCourse(@RequestBody AddTaskRequestDto request, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();
        User userObj = userService.findByUserId(userId);

        //create task
        Task task = taskService.addTask(userId, request.getCourseId(), request.getTitle(), request.getDueDate());

        return ResponseEntity.ok(taskService.toResponse(task));

    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks (@RequestParam(required = false) Long courseId, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();
    
        if (courseId != null) {
            return ResponseEntity.ok(
                taskService.getUserTasksByCourseId(userId, courseId)
            );

        } else {
            return ResponseEntity.ok(
                taskService.getUserTasks(userId)
            );
        }
    }

    @PatchMapping("/task/{id}/complete")
    public ResponseEntity<TaskResponseDto> completeTask(
            @PathVariable Long id,
            @RequestParam boolean completed,
            Authentication auth) {
        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(taskService.completeTask(id, userId, completed));
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        Long userId = ((CustomUserPrincipal) auth.getPrincipal()).getUserId();
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }



}
