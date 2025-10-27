package com.system.mapper;

import com.system.entity.TrainingPlanContest;
import com.system.vo.TrainingPlanDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TrainingPlan-Contest 关联表数据访问层
 */
@Mapper
public interface TrainingPlanContestMapper {
    /**
     * 批量插入训练计划与赛事的关联关系
     * @param items 关联关系列表
     */
    void batchInsert(List<TrainingPlanContest> items);

    /**
     * 根据训练计划ID删除所有关联的赛事
     * @param planId 计划ID
     */
    void deleteByPlanId(Long planId);

    /**
     * 根据训练计划ID查找其包含的所有赛事信息
     * @param planId 计划ID
     * @return 赛事信息VO列表
     */
    List<TrainingPlanDetailVO.ContestInPlanVO> findContestsByPlanId(Long planId);


}

