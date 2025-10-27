package com.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * View Object for contest list item.
 * 用于在赛事列表中，封装返回给前端的单个赛事信息
 */
@Data
public class ContestListVO {

    private Long contestId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String visibility;
    private Long participantCount;
    private String status; // 赛事当前状态：SCHEDULED, ONGOING, ENDED


}
