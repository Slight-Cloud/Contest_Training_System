package com.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.config.FlexibleLocalDateTimeDeserializer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for updating a training plan.
 */
@Data
public class TrainingPlanUpdateDTO {

    @NotNull(message = "计划ID不能为空")
    private Long planId;

    @Size(max = 255, message = "计划标题长度不能超过255个字符")
    private String title;

    private String description;

    @Future(message = "开始时间必须是未来的时间")
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @Future(message = "结束时间必须是未来的时间")
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    private List<Long> contestIds;

    private List<Long> studentIds;
}
