package com.example.campusmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.RegisterRequest;
import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.RegisterService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterServiceImpl(
            UserMapper userMapper,
            BCryptPasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Result<Void> register(RegisterRequest request) {

        if (request.getUsername() == null
                || request.getUsername().trim().isEmpty()) {
            return new Result<>(400, "用户名不能为空", null);
        }

        if (request.getPassword() == null
                || request.getPassword().isEmpty()) {
            return new Result<>(400, "密码不能为空", null);
        }

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {
            return new Result<>(400, "两次密码输入不一致", null);
        }

        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<>();

        queryWrapper.eq(
                User::getUsername,
                request.getUsername().trim()
        );

        User existingUser =
                userMapper.selectOne(queryWrapper);

        if (existingUser != null) {
            return new Result<>(400, "用户名已存在", null);
        }

        User user = new User();

        user.setUsername(request.getUsername().trim());
        user.setName(request.getName());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);

        return new Result<>(200, "注册成功", null);
    }
}
