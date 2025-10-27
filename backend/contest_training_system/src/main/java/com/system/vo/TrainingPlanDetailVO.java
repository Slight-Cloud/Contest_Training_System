package com.system.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * VO for training plan details.
 */
@Data
public class TrainingPlanDetailVO {
    private Long planId;
    private String title;
    private String description;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long creatorId;
    private List<ContestInPlanVO> contests;
    private List<StudentInPlanVO> students;

    @Data
    public static class ContestInPlanVO {
        private Long contestId;
        private String title;
        private Integer sequence;
        private LocalDateTime scheduledTime;
        private String description;
    }

    @Data
    public static class StudentInPlanVO {
        private Long userId;
        private String nickname;
        private LocalDateTime enrolledAt;
    }
}
