package com.example.campusmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.CreateTaskRequest;
import com.example.campusmanagement.entity.Task;
import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.enums.TaskPriority;
import com.example.campusmanagement.enums.TaskStatus;
import com.example.campusmanagement.exception.BusinessException;
import com.example.campusmanagement.mapper.TaskMapper;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    public TaskServiceImpl(
            TaskMapper taskMapper,
            UserMapper userMapper) {

        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Result<Task> createTask(CreateTaskRequest request) {

        User assignee =
                userMapper.selectById(request.getAssigneeId());

        if (assignee == null) {
            throw new BusinessException("负责人不存在");
        }
        if(assignee.getStatus() != 1) {
            throw new BusinessException("该用户已被禁用");
        }
        if(!"USER".equals(assignee.getRole())) {
            throw new BusinessException("只能将任务分配给普通用户");
        }

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setDeadline(request.getDeadline());

        if (request.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        } else {
            task.setPriority(request.getPriority());
        }

        task.setStatus(TaskStatus.TODO);

        taskMapper.insert(task);

        return new Result<>(200, "任务创建成功", task);
    }

    @Override
    public List<Task> getAllTasks(){
        return taskMapper.selectList(null);
    }

    @Override
    public List<Task> getMyTask(Long userId){
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getAssigneeId, userId);
        return taskMapper.selectList(queryWrapper);
    }
}