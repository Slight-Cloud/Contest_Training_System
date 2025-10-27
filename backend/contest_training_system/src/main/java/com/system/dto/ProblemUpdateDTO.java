package com.system.dto;

import lombok.Data;
@Data
public class ProblemUpdateDTO {
    // 字段全部为非必须
    private Long problemId;
    private String title;
    private String description;
    // ... 其他与CreateDTO中类似的字段
    private Integer timeLimit;
    private Integer memoryLimit;
}