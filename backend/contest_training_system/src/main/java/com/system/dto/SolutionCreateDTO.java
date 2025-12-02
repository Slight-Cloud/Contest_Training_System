package com.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolutionCreateDTO {
    @NotNull(message = "题目ID不能为空")
    private Long problemId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private Integer isPublished = 1; // Default to 1 (published)
}
