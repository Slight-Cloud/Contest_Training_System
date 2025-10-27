package com.system.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

/**
 * 题目列表查询的参数封装对象 (DTO)
 */
@Data
public class ProblemQueryDTO {

    // API文档中的所有查询参数都在这里
    // Spring Boot会自动将前端的 a_b 形式的参数映射到驼峰命名(aB)的字段上

    @NonNull
    @Min(value = 1, message = "页码必须从1开始")
    private Integer page=1 ; // 页码，提供默认值

    @NonNull
    @Min(value = 1, message = "每页数量至少为1")
    private Integer pageSize=10 ; // 每页数量，提供默认值

    private String keyword; // 搜索关键字
    private Long creatorId; // 创建者ID
    private Integer isHidden; // 隐藏状态 (0=未隐藏, 1=已隐藏)
    private String  sortBy; // 排序方式 (如 "CREATED_AT_DESC", "DIFFICULTY_ASC" 等)
}