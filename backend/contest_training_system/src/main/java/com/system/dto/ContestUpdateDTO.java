package com.system.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.config.FlexibleLocalDateTimeDeserializer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for updating a contest.
 * 用于封装更新赛事时前端传递的数据
 */

@Data
public class ContestUpdateDTO {

    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    private String title;
    private String description;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @Future(message = "开始时间必须是未来的时间")
    private LocalDateTime startTime;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @Future(message = "结束时间必须是未来的时间")
    private LocalDateTime endTime;
    private String password;
    private String visibility; // "PUBLIC" or "PRIVATE"
    private List<Long> problemIds;

}
