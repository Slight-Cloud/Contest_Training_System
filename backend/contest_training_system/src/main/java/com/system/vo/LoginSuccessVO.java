package com.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功后返回给前端的VO (View Object)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessVO {
    private String token;
}