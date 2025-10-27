package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：用户 (User)
 */
@Data
@Accessors(chain = true)
public class User {
    private Long userId; // 用户主键
    private String email; // 邮箱，唯一
    private String phoneNumber; // 手机号，唯一
    private String nickname; // 昵称
    private String studentId; // 学号，唯一
    private String passwordHash; // 密码哈希
    private String role; // 用户角色（STUDENT/TEACHER/ADMIN）
    private Integer isActive; // 0=注销隐藏, 1=正常
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}