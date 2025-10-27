package com.system.vo;

import com.system.entity.Contest;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View Object for contest details.
 * 用于在赛事详情页，封装返回的完整信息，包括题目列表
 */
@Data
public class ContestDetailVO extends Contest {

    private String status; // 赛事当前状态：SCHEDULED, ONGOING, ENDED
    private List<ProblemInContestVO> problems;
    private Long participantCount;

    /**
     * 内部类，用于表示赛事中的题目信息
     */
   @Data
    public static class ProblemInContestVO {
        private Long problemId;
        private String title;
        private Integer displayOrder;
        private Integer timeLimit;
        private Integer memoryLimit;

    }

}
