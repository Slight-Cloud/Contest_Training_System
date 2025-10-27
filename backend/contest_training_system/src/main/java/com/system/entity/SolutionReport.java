package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：题解报告 (SolutionReport)
 */
@Data
@Accessors(chain = true)
public class SolutionReport {
    private Long reportId; // 题解主键
    private Long problemId; // 所属题目ID
    private Long creatorId; // 创建者用户ID
    private String title; // 题解标题
    private String content; // 题解内容
    private Integer isPublished; // 是否发布（0=草稿, 1=已发布）
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}