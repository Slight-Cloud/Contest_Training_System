package com.system.vo;

import lombok.Data;

import java.util.Date;

@Data
//用于列表页
public class ProblemListStudentVO {
    private String title;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Date createdAt;
}