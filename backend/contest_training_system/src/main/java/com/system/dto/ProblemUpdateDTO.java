package com.system.dto;

import lombok.Data;

@Data
public class ProblemUpdateDTO {
    // 字段全部为非必须，允许部分更新
    private Long problemId;
    private String title;
    private String description;
    private String inputSpec;      // 输入说明
    private String outputSpec;     // 输出说明
    private String sampleInput;    // 样例输入
    private String sampleOutput;   // 样例输出
    private String remark;         // 备注/提示
    private Integer timeLimit;     // 时间限制(ms)
    private Integer memoryLimit;   // 内存限制(MB)
    private Integer isHidden;      // 是否隐藏：0-显示，1-隐藏
}