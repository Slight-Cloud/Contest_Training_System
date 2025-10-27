package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：榜单缓存 (ContestScoreCache) - 联合主键
 */
@Data
@Accessors(chain = true)
public class ContestScoreCache {
    private Long contestId; // 所属赛事ID
    private Long userId; // 用户ID
    private Integer solvedCount; // 解题数
    private Integer penaltyMinutes; // 总罚时（分钟）
    private String perProblemStats; // 每题统计（JSON）
    private LocalDateTime lastUpdated; // 更新时间
}