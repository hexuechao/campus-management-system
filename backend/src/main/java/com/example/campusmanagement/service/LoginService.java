package com.example.campusmanagement.service;

import com.example.campusmanagement.common.Result;

public interface LoginService {
    Result<Void> login(String username, String password);
}
