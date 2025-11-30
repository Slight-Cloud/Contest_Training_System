package com.system.controller;

import com.system.dto.TrainingPlanCreateDTO;
import com.system.dto.TrainingPlanUpdateDTO;
import com.system.dto.TrainingPlanQueryDTO;
import com.system.service.TrainingPlanService;
import com.system.vo.PageResultVO;
import com.system.vo.Result;
import com.system.vo.TrainingPlanDetailVO;
import com.system.vo.TrainingPlanListVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 训练计划管理 Controller
 * 负责处理与训练计划相关的HTTP请求
 */
@RestController
@RequestMapping("/training_plan")
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanService trainingPlanService) {
        this.trainingPlanService = trainingPlanService;
    }

    /**
     * 新建训练计划
     * @param createDTO 训练计划创建信息
     * @return 包含新计划ID的结果
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Long> createTrainingPlan(@Validated @RequestBody TrainingPlanCreateDTO createDTO) {
        Long planId = trainingPlanService.createTrainingPlan(createDTO);
        return Result.success(planId);
    }

    /**
     * 更新训练计划
     * @param updateDTO 训练计划更新信息
     * @return 操作结果
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<?> updateTrainingPlan(@Validated @RequestBody TrainingPlanUpdateDTO updateDTO) {
        trainingPlanService.updateTrainingPlan(updateDTO);
        return Result.success();
    }

    /**
     * 删除训练计划
     * @param planId 计划ID
     * @return 操作结果
     */
    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<?> deleteTrainingPlan(@PathVariable Long planId) {
        trainingPlanService.deleteTrainingPlan(planId);
        return Result.success();
    }

    /**
     * 分页查询训练计划列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResultVO<TrainingPlanListVO>> getTrainingPlanList(TrainingPlanQueryDTO queryDTO) {
        PageResultVO<TrainingPlanListVO> result = trainingPlanService.getTrainingPlanList(queryDTO);
        return Result.success(result);
    }

    /**
     * 获取训练计划详情
     * @param planId 计划ID
     * @return 训练计划详细信息
     */
    @GetMapping("/{planId}")
    public Result<TrainingPlanDetailVO> getTrainingPlanDetail(@PathVariable Long planId) {
        TrainingPlanDetailVO detail = trainingPlanService.getTrainingPlanDetail(planId);
        return Result.success(detail);
    }
}

