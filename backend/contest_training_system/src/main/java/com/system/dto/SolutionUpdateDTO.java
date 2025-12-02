package com.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolutionUpdateDTO {
    @NotNull(message = "题解ID不能为空")
    private Long reportId;

    private String title;
    private String content;
    private Integer isPublished;
}
