package com.example.campusmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.campusmanagement.enums.TaskPriority;
import com.example.campusmanagement.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private Long assigneeId;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime deadline;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
