package com.example.campusmanagement.service.impl;

import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers(String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim();

            queryWrapper.and(wrapper -> wrapper.like(User::getUsername, trimmedKeyword)
                        .or()
                        .like(User::getName,trimmedKeyword));
        }
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public boolean createUser(User user) {
        user.setId(null);
        user.setCreateTime(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(Long id, User user) {
        user.setId(id);
        user.setPassword(null);
        user.setCreateTime(null);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public User getUserByUsername(String username){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        return userMapper.selectOne(queryWrapper);
    }
}
