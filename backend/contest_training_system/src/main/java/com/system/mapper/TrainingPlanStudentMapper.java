package com.system.mapper;

import com.system.entity.TrainingPlanStudent;
import com.system.vo.TrainingPlanDetailVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * TrainingPlan-Student 关联表数据访问层
 */
@Mapper
public interface TrainingPlanStudentMapper {
    /**
     * 批量插入训练计划与学生的关联关系
     * @param items 关联关系列表
     */
    void batchInsert(List<TrainingPlanStudent> items);

    /**
     * 根据训练计划ID删除所有关联的学生
     * @param planId 计划ID
     */
    void deleteByPlanId(Long planId);

    /**
     * 根据训练计划ID查找其包含的所有学生信息
     * @param planId 计划ID
     * @return 学生信息VO列表
     */
    List<TrainingPlanDetailVO.StudentInPlanVO> findStudentsByPlanId(Long planId);
}

