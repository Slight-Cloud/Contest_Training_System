package com.system.vo;

import lombok.Data;
import java.util.Date;

@Data
//用于列表页
public class ProblemListVO {
    private Long problemId;
    private String title;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Long creatorId;
    private Integer isHidden;
    private Date createdAt;
}