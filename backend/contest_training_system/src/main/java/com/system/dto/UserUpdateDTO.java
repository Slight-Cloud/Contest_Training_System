package com.system.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    // 字段都是非必须的
    private String nickname;
    private String phoneNumber;
    private String email;
    private String studentId;
    private String password; // 新密码
}