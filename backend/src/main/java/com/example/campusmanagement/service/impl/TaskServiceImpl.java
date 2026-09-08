package com.example.campusmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.CreateTaskRequest;
import com.example.campusmanagement.entity.Task;
import com.example.campusmanagement.entity.TaskLog;
import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.enums.TaskPriority;
import com.example.campusmanagement.enums.TaskStatus;
import com.example.campusmanagement.exception.BusinessException;
import com.example.campusmanagement.mapper.TaskLogMapper;
import com.example.campusmanagement.mapper.TaskMapper;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final TaskLogMapper taskLogMapper;

    public TaskServiceImpl(
            TaskMapper taskMapper,
            UserMapper userMapper,
            TaskLogMapper taskLogMapper) {

        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
        this.taskLogMapper = taskLogMapper;
    }

    @Override
    public Result<Task> createTask(CreateTaskRequest request, User currentUser) {

        LambdaQueryWrapper<User> userQueryWrapper =
                new LambdaQueryWrapper<>();

        userQueryWrapper.eq(
                User::getUsername,
                request.getAssigneeUsername()
        );

        User assignee =
                userMapper.selectOne(userQueryWrapper);

        if (assignee == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "负责人不存在");
        }
        if(assignee.getStatus() != 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该用户已被禁用");
        }
        if(!"USER".equals(assignee.getRole())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只能将任务分配给普通用户");
        }

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(assignee.getId());
        task.setDeadline(request.getDeadline());

        if (request.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        } else {
            task.setPriority(request.getPriority());
        }

        task.setStatus(TaskStatus.TODO);

        taskMapper.insert(task);

        TaskLog taskLog = new TaskLog();

        taskLog.setTaskId(task.getId());
        taskLog.setOperatorUsername(currentUser.getUsername());
        taskLog.setAction("CREATE");

        taskLogMapper.insert(taskLog);

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

    @Override
    public void updateTaskStatus(
            Long taskId,
            User currentUser,
            TaskStatus newStatus) {

        // 1. 查询任务
        Task task = taskMapper.selectById(taskId);

        // 2. 判断任务是否存在
        if (task == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "任务不存在");
        }

        // 3. 判断任务是否属于当前登录用户
        if (!Objects.equals(task.getAssigneeId(), currentUser.getId())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "无权修改该任务");
        }

        // 4. 获取任务当前状态
        TaskStatus oldStatus = task.getStatus();

        // 5. 判断状态流转是否合法
        boolean validTransition =
                (oldStatus == TaskStatus.TODO
                        && newStatus == TaskStatus.IN_PROGRESS)
                        ||
                        (oldStatus == TaskStatus.IN_PROGRESS
                                && newStatus == TaskStatus.DONE);

        if (!validTransition) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "非法的任务状态流转");
        }

        // 6. 修改状态
        task.setStatus(newStatus);

        // 7. 更新数据库
        taskMapper.updateById(task);

        TaskLog taskLog = new TaskLog();

        taskLog.setTaskId(taskId);
        taskLog.setOperatorUsername(currentUser.getUsername());
        taskLog.setAction(oldStatus + " -> " + newStatus);

        taskLogMapper.insert(taskLog);
    }
}