package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：题目 (Problem)
 */
@Data
@Accessors(chain = true)
public class Problem {
    private Long problemId; // 题目主键
    private String title; // 题目标题
    private String description; // 题目描述
    private String inputSpec; // 输入说明
    private String outputSpec; // 输出说明
    private String sampleInput; // 输入样例
    private String sampleOutput; // 输出样例
    private String remark; // 备注
    private Integer timeLimit; // 时间限制（ms）
    private Integer memoryLimit; // 内存限制（MB）
    private Long creatorId; // 创建者用户ID
    private Integer isHidden; // 隐藏状态：1=隐藏, 0=显示
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}