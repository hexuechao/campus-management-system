package com.example.campusmanagement.controller;

import com.example.campusmanagement.entity.User;
import com.example.campusmanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> listUsers(@RequestParam(required = false) String keyword) {
        return userService.listUsers(keyword);
    }

    @PostMapping
    public boolean createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public boolean updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/me")
    public String getCurrentUser(
            HttpServletRequest request) {

        return (String) request.getAttribute("username");
    }
}
