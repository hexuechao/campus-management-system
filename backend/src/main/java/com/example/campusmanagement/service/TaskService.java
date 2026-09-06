package com.example.campusmanagement.service;

import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.CreateTaskRequest;
import com.example.campusmanagement.entity.Task;

public interface TaskService {

    Result<Task> createTask(CreateTaskRequest request);

}
