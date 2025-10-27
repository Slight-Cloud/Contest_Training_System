package com.system.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：测试点结果 (SubmissionTestcase) - 联合主键
 */
@Data
@Accessors(chain = true)
public class SubmissionTestcase {
    private Long submissionId; // 所属提交ID
    private Integer caseIndex; // 测试点序号
    private Integer datasetVersion; // 测试数据版本
    private String result; // 评测结果
    private Integer timeUsed; // 用时
    private Integer memoryUsed; // 内存
    private String message; // 评测消息
}