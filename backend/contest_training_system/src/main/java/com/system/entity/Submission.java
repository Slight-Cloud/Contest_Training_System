package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：提交 (Submission)
 */
@Data
@Accessors(chain = true)
public class Submission {
    private Long submissionId; // 提交主键
    private Long contestId; // 所属赛事ID
    private Long problemId; // 所属题目ID
    private Long userId; // 提交用户ID
    private String code; // 提交代码
    private Integer codeLength; // 代码长度
    private String language; // 编程语言（如C/C++/Java/Python）
    private String compiler; // 编译器版本
    private String result; // 评测结果（AC/WA/RE/CE/TLE/MLE/PE）
    private Integer timeUsed; // 用时（ms）
    private Integer memoryUsed; // 内存（KB）
    private LocalDateTime createdAt; // 提交时间
    private String judgeLogUrl; // 评测日志地址
}