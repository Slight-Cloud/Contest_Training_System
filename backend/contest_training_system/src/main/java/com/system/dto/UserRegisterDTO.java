package com.system.dto;

import lombok.Data;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册时接收前端数据的DTO (Data Transfer Object)
 */

@Data
public class UserRegisterDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    @NotBlank(message = "手机号不能为空")
    // 国内 11 位简单校验，可按需要调整
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度需在2-20字符之间")
    private String nickname;

    @NotBlank(message = "学号不能为空")
    @Size(min = 1, max = 32, message = "学号长度需在1-32字符之间")
    private String studentId;

    @NotBlank(message = "密码不能为空")
    // 至少 6 位，含字母与数字（可再加特殊字符要求）
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]|:;\"'<>,.?/]{6,32}$",
            message = "密码需6-32位并包含字母和数字")
    private String password;
}
