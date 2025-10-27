package com.system.dto;

import lombok.Data;
@Data
public class ProblemCreateDTO {
    private String title;
    private String description;
    private String inputSpec;
    private String outputSpec;
    private String sampleInput;
    private String sampleOutput;
    private String remark;
    private Integer timeLimit;
    private Integer memoryLimit;
    // testdata_zip 在API文档中是必须的，这里我们假设它是一个URL字符串
    private String testdataZip;
}