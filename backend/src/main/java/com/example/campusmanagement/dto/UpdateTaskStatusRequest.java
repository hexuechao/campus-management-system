package com.example.campusmanagement.dto;

import com.example.campusmanagement.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {

    @NotNull(message = "任务状态不能为空")
    private TaskStatus taskStatus;

}
