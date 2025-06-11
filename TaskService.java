package com.kaiburr.taskapi.service;

import com.kaiburr.taskapi.model.Task;
import com.kaiburr.taskapi.model.TaskExecution;
import com.kaiburr.taskapi.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> findTasksByName(String name) {
        return taskRepository.findByNameContainingIgnoreCase(name);
    }

    public Task saveTask(Task task) throws IllegalArgumentException {
        if (!isSafeCommand(task.getCommand())) {
            throw new IllegalArgumentException("Unsafe command.");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public Task executeTaskCommand(String id) throws Exception {
        Task task = taskRepository.findById(id).orElseThrow(() -> new Exception("Task not found"));
        TaskExecution exec = new TaskExecution();
        exec.setStartTime(new Date());

        Process process = Runtime.getRuntime().exec(task.getCommand());
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();
        exec.setEndTime(new Date());
        exec.setOutput(output.toString().trim());

        task.getTaskExecutions().add(exec);
        return taskRepository.save(task);
    }

    private boolean isSafeCommand(String command) {
        String[] dangerous = {"rm", "sudo", "shutdown", "reboot", "kill", ">", "<", "|", "&"};
        for (String d : dangerous) {
            if (command.contains(d)) return false;
        }
        return true;
    }
}
