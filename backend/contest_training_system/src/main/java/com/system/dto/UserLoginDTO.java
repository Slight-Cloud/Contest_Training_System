package com.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录时接收前端数据的DTO
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "邮箱或手机号不能为空")
    private String emailOrPhone;
    @NotBlank(message = "密码不能为空")
    private String password;
}