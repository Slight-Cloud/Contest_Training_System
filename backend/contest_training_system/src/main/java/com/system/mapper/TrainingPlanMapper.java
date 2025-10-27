package com.system.mapper;

import com.system.dto.TrainingPlanQueryDTO;
import com.system.entity.TrainingPlan;
import com.system.vo.TrainingPlanListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TrainingPlan 数据访问层
 */
@Mapper
public interface TrainingPlanMapper {
    /**
     * 插入一条新的训练计划
     *
     * @param trainingPlan 训练计划实体
     */
    void insert(TrainingPlan trainingPlan);

    /**
     * 更新训练计划信息
     *
     * @param trainingPlan 待更新的训练计划实体
     */
    void update(TrainingPlan trainingPlan);

    /**
     * 根据ID删除训练计划
     *
     * @param planId 计划ID
     */
    void deleteById(Long planId);

    /**
     * 根据ID查询训练计划
     *
     * @param planId 计划ID
     * @return 训练计划实体
     */
    TrainingPlan getById(Long planId);

    /**
     * 根据标题查询训练计划
     *
     * @param title 计划标题
     * @return 训练计划实体
     */
    TrainingPlan getByTitle(String title);

    /**
     * 分页查询训练计划列表 (优化版)
     *
     * @param queryDTO      查询条件 DTO
     * @param now           当前时间，用于动态计算状态
     * @param sortColumn    排序列名 (安全)
     * @param sortDirection 排序方向 (安全)
     * @return 分页后的训练计划列表
     */
    List<TrainingPlanListVO> pageQuery(
            @Param("role") String role,
            @Param("userId") Long userId,
            @Param("query") TrainingPlanQueryDTO queryDTO,
            @Param("now") LocalDateTime now,
            @Param("sortColumn") String sortColumn,
            @Param("sortDirection") String sortDirection
    );

    /**
     * 查询该训练计划的赛事列表
     */
    @Select("SELECT contest_id FROM training_plan_contest WHERE plan_id = #{planId} ORDER BY sequence ASC, added_time ASC")
    List<Long> getContestIdsByPlanId(Long planId);

    /**
     * 查询该训练计划的学员列表
     */
    @Select("SELECT user_id FROM training_plan_student WHERE plan_id = #{planId} ORDER BY enrolled_at ASC")
    List<Long> getStudentIdsByPlanId(Long planId);

    @Select("SELECT creator_id FROM training_plan WHERE plan_id = #{planId}")
    Long getCreatorIdByPlanId(Long planId);
}

