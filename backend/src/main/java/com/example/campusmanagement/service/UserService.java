package com.example.campusmanagement.service;

import com.example.campusmanagement.entity.User;

import java.util.List;

public interface UserService {

    List<User> listUsers(String keyword);

    boolean createUser(User user);

    boolean updateUser(Long id, User user);

    boolean deleteUser(Long id);

    public User getUserByUsername(String username);
}
