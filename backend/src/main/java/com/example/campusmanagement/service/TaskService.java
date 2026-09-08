package com.example.campusmanagement.service;

import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.CreateTaskRequest;
import com.example.campusmanagement.entity.Task;
import com.example.campusmanagement.enums.TaskStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TaskService {

    Result<Task> createTask(CreateTaskRequest request);

    List<Task> getAllTasks();

    List<Task> getMyTask(Long userId);

    void updateTaskStatus(
            Long taskId,
            Long currentUserId,
            TaskStatus newStatus
    );
}
