package com.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * VO for displaying a list of training plans.
 */
@Data
public class TrainingPlanListVO {
    private Long planId;
    private String title;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long creatorId;
    private Long studentCount;
    private Long contestCount;
}
