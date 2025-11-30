package com.system.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

/**
 * DTO for querying training plans.
 */
@Data
public class TrainingPlanQueryDTO {

    @NonNull
    @Min(value = 1, message = "页码不能小于1")
    private int page = 1;
    @NonNull
    @Min(value = 1, message = "每页数量不能小于1")
    private int pageSize = 10;
    private Long creatorId;
    private String keyword;
    private String status;  // 计划状态：SCHEDULED/ONGOING/ENDED
    private String sortBy;
}
