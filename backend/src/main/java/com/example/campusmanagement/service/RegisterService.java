package com.example.campusmanagement.service;
import com.example.campusmanagement.common.Result;
import com.example.campusmanagement.dto.RegisterRequest;

public interface RegisterService {

    Result<Void> register(RegisterRequest request);

}
