package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：训练计划赛事 (TrainingPlanContest) - 联合主键
 */
@Data
@Accessors(chain = true)
public class TrainingPlanContest {
    private Long planId; // 所属训练计划ID
    private Long contestId; // 所属赛事ID
    private String description; // 赛事安排说明
    private Integer sequence; // 安排顺序
    private LocalDateTime addedTime; // 计划时间
}