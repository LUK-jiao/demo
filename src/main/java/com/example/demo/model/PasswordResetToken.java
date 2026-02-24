package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Date;

@Data
public class PasswordResetToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String tokenHash;

    @TableField(fill = FieldFill.INSERT)
    private Date expiresAt;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private Boolean used;
}