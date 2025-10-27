package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：赛事 (Contest)
 */
@Data
@Accessors(chain = true)
public class Contest {
    private Long contestId; // 赛事主键
    private String title; // 赛事标题
    private String description; // 赛事介绍
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private String visibility; // PUBLIC/PRIVATE
    private String state; // HIDDEN/USING
    private String passwordHash; // 访问密码哈希
    private Long creatorId; // 创建者用户ID
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}