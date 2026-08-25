package com.example.campusmanagement.service.impl;

import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.mapper.UserMapper;
import com.example.campusmanagement.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> listUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public boolean createUser(User user) {
        user.setId(null);
        user.setCreateTime(null);
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
}
