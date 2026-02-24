package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PasswordResetTokenMapper extends BaseMapper<PasswordResetToken> {
}