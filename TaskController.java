package com.kaiburr.taskapi.controller;

import com.kaiburr.taskapi.model.Task;
import com.kaiburr.taskapi.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
@GetMapping("/")
public String home() {
    return "Task API is running!";
}

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            return taskService.getTaskById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/search")
    public ResponseEntity<?> findByName(@RequestParam String name) {
        List<Task> found = taskService.findTasksByName(name);
        if (found.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found);
    }

    @PutMapping
    public ResponseEntity<?> putTask(@RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.saveTask(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTask(@RequestParam String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/execute/{id}")
    public ResponseEntity<?> executeCommand(@PathVariable String id) {
        try {
            return ResponseEntity.ok(taskService.executeTaskCommand(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
