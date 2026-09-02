package com.example.campusmanagement.controller;

import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.RegisterRequest;
import com.example.campusmanagement.service.RegisterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {this.registerService = registerService;}

    @PostMapping
    public Result<Void> register(@RequestBody RegisterRequest registerRequest) {
        return registerService.register(registerRequest);
    }
}
