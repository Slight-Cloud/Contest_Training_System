package com.system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体类：题目测试数据 (ProblemDataset)
 */
@Data
@Accessors(chain = true)
public class ProblemDataset {
    private Long datasetId; // 测试数据主键
    private Long problemId; // 所属题目ID
    private String zipUrl; // 测试数据ZIP文件地址
    private Integer version; // 版本号，题目内唯一
    private Integer isActive; // 1=可用, 0=停用
    private Long addedBy; // 添加者用户ID
    private LocalDateTime addedAt; // 添加时间
}