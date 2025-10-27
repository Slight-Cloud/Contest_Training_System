package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：参赛者 (ContestParticipant) - 联合主键
 */
@Data
@Accessors(chain = true)
public class ContestParticipant {
    private Long contestId; // 所属赛事ID
    private Long userId; // 参赛用户ID
    private LocalDateTime registeredAt; // 注册/加入时间
    private Integer isOfficial; // 0=练习, 1=正式参赛
    private String teamName; // 队伍名
}