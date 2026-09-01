package com.example.campusmanagement.controller;

import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.LoginRequest;
import com.example.campusmanagement.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {this.loginService = loginService;}

    @PostMapping
    public Result<Void> login(@RequestBody LoginRequest request) {
        return loginService.login(request.getUsername(), request.getPassword());
    }
}
