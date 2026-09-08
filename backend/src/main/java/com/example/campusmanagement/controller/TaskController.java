package com.example.campusmanagement.controller;

import com.example.campusmanagement.annotation.RequireRole;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.CreateTaskRequest;
import com.example.campusmanagement.dto.UpdateTaskStatusRequest;
import com.example.campusmanagement.entity.Task;
import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.service.TaskService;
import com.sun.source.doctree.AttributeTree;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {this.taskService = taskService;}

    @RequireRole("ADMIN")
    @PostMapping
    public Result<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    @RequireRole("ADMIN")
    @GetMapping
    public Result<List<Task>> getAllTasks(){
        List<Task> task = taskService.getAllTasks();
        return new Result<>(200, "查询成功", task);
    }

    @GetMapping("/my")
    public Result<List<Task>> getMyTask(HttpServletRequest request){
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser.getId();
        List<Task> tasks = taskService.getMyTask(userId);
        return new Result<>(200, "查询成功", tasks);
    }

    @PatchMapping("{id}/status")
    public Result<Void> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser.getId();
        taskService.updateTaskStatus(id, userId, updateTaskStatusRequest.getTaskStatus());
        return new Result<>(200, "修改成功", null);
    }

}
