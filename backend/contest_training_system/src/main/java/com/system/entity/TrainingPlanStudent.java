package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：训练计划学生 (TrainingPlanStudent) - 联合主键
 */
@Data
@Accessors(chain = true)
public class TrainingPlanStudent {
    private Long planId; // 所属训练计划ID
    private Long userId; // 学生用户ID
    private LocalDateTime enrolledAt; // 加入时间
}