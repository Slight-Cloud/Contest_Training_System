package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：训练计划 (TrainingPlan)
 */
@Data
@Accessors(chain = true)
public class TrainingPlan {
    private Long planId; // 训练计划主键
    private String title; // 计划标题
    private String description; // 训练计划说明
    private String status; // NOT_STARTED/ONGOING/COMPLETED
    private Long creatorId; // 创建者用户ID
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}