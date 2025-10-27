package com.system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * DTO for creating a contest.
 * 用于封装创建赛事时前端传递的数据
 */
@Data
public class ContestCreateDTO {

    @NotBlank(message = "赛事标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String password;

    @NotBlank(message = "赛事可见性不能为空")
    private String visibility; // "PUBLIC" or "PRIVATE"

    @NotEmpty(message = "题目列表不能为空")
    private List<Long> problemIds;


}
