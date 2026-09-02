package com.example.campusmanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.LoginService;
import com.example.campusmanagement.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Result<String> login(String username, String password) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return new Result<>(401, "用户名或密码错误", null);
        }

        if(!passwordEncoder.matches(password,user.getPassword())){
            return new Result<>(401, "用户名或密码错误", null);
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new Result<>(200, "登录成功", token);
    }

}
