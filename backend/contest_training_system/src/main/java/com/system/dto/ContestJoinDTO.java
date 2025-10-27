package com.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for joining a contest.
 * 用于封装用户报名参加赛事时传递的数据
 */
@Data
public class ContestJoinDTO {
    @NotNull(message = "赛事ID不能为空")
    private Long contestId;
    private String password;
    private String teamName;

}

