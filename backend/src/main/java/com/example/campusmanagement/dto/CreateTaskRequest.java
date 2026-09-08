package com.example.campusmanagement.dto;

import com.example.campusmanagement.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题不能超过100个字符")
    private String title;

    private String description;

    @NotBlank(message = "负责人用户名不能为空")
    private String assigneeUsername;

    private TaskPriority priority;

    private LocalDateTime deadline;

}
