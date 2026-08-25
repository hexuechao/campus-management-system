package com.example.campusmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campusmanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
