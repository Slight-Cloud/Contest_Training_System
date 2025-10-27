package com.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserProfileVO {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String studentId;
    private String role;
    private LocalDateTime createdAt;
    private Integer isActive;

}