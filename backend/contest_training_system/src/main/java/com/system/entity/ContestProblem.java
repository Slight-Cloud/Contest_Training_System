package com.system.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：赛事题目 (ContestProblem) - 联合主键
 */
@Data
@Accessors(chain = true)
public class ContestProblem {
    private Long contestId; // 所属赛事ID
    private Long problemId; // 所属题目ID
    private Integer displayOrder; // 题目顺序
}