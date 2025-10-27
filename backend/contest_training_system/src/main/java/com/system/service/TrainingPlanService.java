package com.system.service;

import com.system.dto.TrainingPlanCreateDTO;
import com.system.dto.TrainingPlanUpdateDTO;
import com.system.dto.TrainingPlanQueryDTO;
import com.system.vo.TrainingPlanDetailVO;
import com.system.vo.TrainingPlanListVO;
import com.system.vo.PageResultVO;

/**
 * 训练计划业务逻辑接口
 */
public interface TrainingPlanService {

    /**
     * 创建训练计划
     * @param createDTO 包含计划信息、关联赛事和学生的DTO
     * @return 新创建的训练计划ID
     */
    Long createTrainingPlan(TrainingPlanCreateDTO createDTO);

    /**
     * 更新训练计划
     * @param updateDTO 包含待更新信息的DTO
     */
    void updateTrainingPlan(TrainingPlanUpdateDTO updateDTO);

    /**
     * 删除训练计划
     * @param planId 待删除的计划ID
     */
    void deleteTrainingPlan(Long planId);

    /**
     * 分页查询训练计划列表
     * @param queryDTO 包含分页和筛选条件的DTO
     * @return 封装好的分页视图对象
     */
    PageResultVO<TrainingPlanListVO> getTrainingPlanList(TrainingPlanQueryDTO queryDTO);

    /**
     * 获取训练计划详情
     * @param planId 计划ID
     * @return 包含赛事和学生列表的详细视图对象
     */
    TrainingPlanDetailVO getTrainingPlanDetail(Long planId);
}

