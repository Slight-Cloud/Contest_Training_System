package com.system.vo;

import com.system.entity.ProblemDataset; // 假设你已有此实体
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
//用于详情页
public class ProblemDetailVO {
    private Long problemId;
    private String title;
    private String description;
    private String inputSpec;
    private String outputSpec;
    private String sampleInput;
    private String sampleOutput;
    private String remark;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Long creatorId;
    private Date createdAt;
    // 包含测试数据集信息
    private List<ProblemDataset> datasets;
}