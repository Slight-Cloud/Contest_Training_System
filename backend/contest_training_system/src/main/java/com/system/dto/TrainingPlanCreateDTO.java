package com.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for creating a training plan.
 */
@Data
public class TrainingPlanCreateDTO {

    @NotEmpty(message = "计划标题不能为空")
    @Size(max = 255, message = "计划标题长度不能超过255个字符")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    private LocalDateTime endTime;

    @NotEmpty(message = "必须至少关联一个赛事")
    private List<Long> contestIds;

    @NotEmpty(message = "必须至少关联一个学生")
    private List<Long> studentIds;
}
