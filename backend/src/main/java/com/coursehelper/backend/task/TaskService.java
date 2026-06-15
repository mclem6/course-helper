package com.coursehelper.backend.task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coursehelper.backend.task.dto.TaskResponseDto;

@Service
public class TaskService {

    TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }


       

    public Task addTask(Long userId, Long courseId, String title, LocalDate dueDate){

        Task task; 

        if (dueDate != null){
            task = new Task(userId, courseId, title, dueDate, false);
        } else{
            task = new Task(userId, courseId, title, false);
        }

        taskRepository.save(task);

        return task;
    }


    public Map<Long, List<TaskResponseDto>> getUserTasks(Long userId){
        List<Task> tasks = taskRepository.findByUserIdAndCompleted(userId, false);
        return tasks.stream()
        .map(this::toResponse)
        .collect(Collectors.groupingBy(TaskResponseDto::getCourseId));
    }


    public TaskResponseDto toResponse(Task task){
        return new TaskResponseDto(task.getId(), task.getCourseId(), task.getUserId(), task.getTitle(), task.getDueDate(), task.getCompleted());
    }

    public List<TaskResponseDto> getUserTasksByCourseId(Long userId, Long courseId){
        List<Task> tasks = taskRepository.findByCourseIdAndUserId(courseId, userId);
        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponseDto completeTask(Long taskId, Long userId, boolean completed) {
        Task task = taskRepository.findById(taskId)
            .filter(t -> t.getUserId().equals(userId))
            .orElseThrow(() -> new com.coursehelper.backend.exceptions.ResourceNotFoundException("Task not found"));
        task.setCompleted(completed);
        taskRepository.save(task);
        return toResponse(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .filter(t -> t.getUserId().equals(userId))
            .orElseThrow(() -> new com.coursehelper.backend.exceptions.ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }



}
