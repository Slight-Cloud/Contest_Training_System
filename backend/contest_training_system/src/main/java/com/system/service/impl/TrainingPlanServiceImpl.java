package com.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.system.dto.TrainingPlanCreateDTO;
import com.system.dto.TrainingPlanQueryDTO;
import com.system.dto.TrainingPlanUpdateDTO;
import com.system.entity.SortOrder;
import com.system.entity.TrainingPlan;
import com.system.entity.TrainingPlanContest;
import com.system.entity.TrainingPlanStudent;
import com.system.exception.BusinessException;
import com.system.mapper.ContestMapper;
import com.system.mapper.TrainingPlanContestMapper;
import com.system.mapper.TrainingPlanMapper;
import com.system.mapper.TrainingPlanStudentMapper;
import com.system.service.TrainingPlanService;
import com.system.util.UserContext;
import com.system.vo.PageResultVO;
import com.system.vo.TrainingPlanDetailVO;
import com.system.vo.TrainingPlanListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * a)教师可以新建一个训练计划，将参与的学生加入到训练计划中。训练计划包括若干场赛事的具体安排。
 * b)教师可以修改训练计划中未进行的部分。
 * c)未开始的训练计划可以直接删除，已开始的训练计划只能修改不能删除。
 */
@Service
public class TrainingPlanServiceImpl implements TrainingPlanService {

    private final TrainingPlanMapper trainingPlanMapper;
    private final TrainingPlanContestMapper trainingPlanContestMapper;
    private final TrainingPlanStudentMapper trainingPlanStudentMapper;
    private final ContestMapper contestMapper;


    public TrainingPlanServiceImpl(TrainingPlanMapper trainingPlanMapper,
                                   TrainingPlanContestMapper trainingPlanContestMapper,
                                   TrainingPlanStudentMapper trainingPlanStudentMapper,
                                   ContestMapper contestMapper) {
        this.contestMapper = contestMapper;
        this.trainingPlanMapper = trainingPlanMapper;
        this.trainingPlanContestMapper = trainingPlanContestMapper;
        this.trainingPlanStudentMapper = trainingPlanStudentMapper;
    }

    @Override
    @Transactional
    public Long createTrainingPlan(TrainingPlanCreateDTO createDTO) {
        // 1. 业务校验(时间逻辑,至于标题唯一性已经在数据库层面做了唯一索引)
        if (createDTO.getEndTime().isBefore(createDTO.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }


        // 2. 构造并插入训练计划主记录

        TrainingPlan plan = new TrainingPlan();
        BeanUtils.copyProperties(createDTO, plan);
        plan.setCreatorId(UserContext.getUserId());
        plan.setStatus("NOT_STARTED"); // 初始状态固定为“未开始”
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        trainingPlanMapper.insert(plan);

        Long planId = plan.getPlanId();

        // 3. 批量关联赛事
        associateContests(planId, createDTO.getContestIds());

        // 4. 批量关联学生
        associateStudents(planId, createDTO.getStudentIds());

        return planId;
    }

    @Override
    @Transactional
    public void updateTrainingPlan(TrainingPlanUpdateDTO updateDTO) {
        // 1. 查找计划是否存在
        TrainingPlan plan = trainingPlanMapper.getById(updateDTO.getPlanId());
        if (plan == null) {
            throw new BusinessException("训练计划不存在");
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. 检查计划是否已结束，已结束的计划完全不允许修改
        if (plan.getEndTime().isBefore(now)) {
            throw new BusinessException("训练计划已结束，无法修改");
        }

        boolean hasStarted = plan.getStartTime().isBefore(now);

        // 3. 如果计划已开始，施加更严格的修改限制
        Long planId = plan.getPlanId();
        if (hasStarted) {
            // 对于已开始的计划，不允许修改开始时间
            LocalDateTime newStartTime = updateDTO.getStartTime();
            if (newStartTime != null && !newStartTime.isAfter(plan.getStartTime())) {
                throw new BusinessException("训练计划已开始，无法修改开始时间");
            }

            //获取学生列表
            List<Long> studentIdsInPlan = trainingPlanMapper.getStudentIdsByPlanId(planId);
            // 不允许修改学生列表
            if (!CollectionUtils.isEmpty(updateDTO.getStudentIds()) &&
                    !studentIdsInPlan.equals(updateDTO.getStudentIds())) {
                throw new BusinessException("训练计划已开始，无法修改关联的学生列表");
            }

            // 获取原有的赛事列表，对比当前的赛事列表，禁止删除已开始或已结束的赛事
            List<Long> contestIdsInPlan = trainingPlanMapper.getContestIdsByPlanId(planId);
            // 校验赛事修改：允许增删未来的赛事，但禁止删除已开始或已结束的赛事
            if (updateDTO.getContestIds() != null) {
                validateContestChanges(contestIdsInPlan, updateDTO.getContestIds());
            }
        }

        // 4. 更新主记录属性
        // 对于未开始的计划，所有字段都可以更新
        // 对于已开始的计划，受限于上面的校验，只能更新title, description, endTime等
        BeanUtils.copyProperties(updateDTO, plan);
        plan.setUpdatedAt(LocalDateTime.now());
        trainingPlanMapper.update(plan);

        // 5. 对于未开始的计划，可以更新关联的赛事和学生 (采用先删后插的策略)
        if (!hasStarted) {
            if (updateDTO.getContestIds() != null) {
                trainingPlanContestMapper.deleteByPlanId(planId);
                associateContests(planId, updateDTO.getContestIds());
            }
            if (updateDTO.getStudentIds() != null) {
                trainingPlanStudentMapper.deleteByPlanId(planId);
                associateStudents(planId, updateDTO.getStudentIds());
            }
        }
    }

    @Override
    @Transactional
    public void deleteTrainingPlan(Long planId) {
        // 1. 查找计划是否存在
        TrainingPlan plan = trainingPlanMapper.getById(planId);
        if (plan == null) {
            throw new BusinessException("训练计划不存在");
        }

        // 2. 实时计算状态，进行业务校验：已开始的计划不允许删除
        if (plan.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("训练计划已开始，无法删除");
        }

        // 3. 删除所有关联记录
        trainingPlanContestMapper.deleteByPlanId(planId);
        trainingPlanStudentMapper.deleteByPlanId(planId);

        // 4. 删除主记录
        trainingPlanMapper.deleteById(planId);
    }

    @Override
    public PageResultVO<TrainingPlanListVO> getTrainingPlanList(TrainingPlanQueryDTO queryDTO) {

        // 0. 根据用户角色，设置查询范围
        String role = UserContext.getRole();
        Long userId = UserContext.getUserId();

        // 1. 准备所有需要的参数
        LocalDateTime now = LocalDateTime.now();
        SortOrder sortOrder = SortOrder.forTrainingPlan(queryDTO.getSortBy());
        String sortColumn = sortOrder.getColumn();
        String sortDirection = sortOrder.getDirection();

        // 2. 开启分页
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());

        // 3. 执行数据库查询，传入所有参数
        Page<TrainingPlanListVO> page = (Page<TrainingPlanListVO>) trainingPlanMapper.pageQuery(
                role,
                userId,
                queryDTO,
                now,
                sortColumn,
                sortDirection
        );

        // 4. 封装并返回分页结果
        return new PageResultVO<>(page.getTotal(), queryDTO.getPage(), queryDTO.getPageSize(), page.getResult());
    }

    @Override
    public TrainingPlanDetailVO getTrainingPlanDetail(Long planId) {

        //0.需要根据角色判断是否有权限查看该训练计划
        String role = UserContext.getRole();
        Long userId = UserContext.getUserId();
        if ("TEACHER".equals(role)) {
            Long creatorId = trainingPlanMapper.getCreatorIdByPlanId(planId);
            if (!userId.equals(creatorId)) {
                throw new BusinessException("无权限查看该训练计划");
            }
        } else if ("STUDENT".equals(role)) {
            List<Long> studentIds = trainingPlanMapper.getStudentIdsByPlanId(planId);
            if (!studentIds.contains(userId)) {
                throw new BusinessException("无权限查看该训练计划");
            }
        }

        // 1. 查询主记录
        TrainingPlan plan = trainingPlanMapper.getById(planId);
        if (plan == null) {
            throw new BusinessException("训练计划不存在");
        }

        // 2. 构造VO并进行实时状态计算
        TrainingPlanDetailVO detailVO = new TrainingPlanDetailVO();
        BeanUtils.copyProperties(plan, detailVO);

        LocalDateTime now = LocalDateTime.now();
        if (detailVO.getEndTime().isBefore(now)) {
            detailVO.setStatus("ENDED");
        }
        else if (detailVO.getStartTime().isAfter(now)) {
            detailVO.setStatus("SCHEDULED");
        }
        else {
            detailVO.setStatus("ONGOING");
        }

        // 3. 查询关联的赛事和学生列表
        detailVO.setContests(trainingPlanContestMapper.findContestsByPlanId(planId));
        detailVO.setStudents(trainingPlanStudentMapper.findStudentsByPlanId(planId));

        return detailVO;
    }

    /**
     * 辅助方法：关联赛事到训练计划
     */
    private void associateContests(Long planId, List<Long> contestIds) {
        if (!CollectionUtils.isEmpty(contestIds)) {
            List<TrainingPlanContest> planContests = new ArrayList<>();
            for (int i = 0; i < contestIds.size(); i++) {
                TrainingPlanContest tpc = new TrainingPlanContest();
                tpc.setPlanId(planId);
                tpc.setContestId(contestIds.get(i));
                tpc.setSequence(i + 1); // 按列表顺序作为安排顺序
                tpc.setAddedTime(LocalDateTime.now()); // 默认计划时间为创建时间，可后续扩展
                planContests.add(tpc);
            }
            trainingPlanContestMapper.batchInsert(planContests);
        }
    }

    /**
     * 辅助方法：关联学生到训练计划
     */
    private void associateStudents(Long planId, List<Long> studentIds) {
        if (!CollectionUtils.isEmpty(studentIds)) {
            List<TrainingPlanStudent> planStudents = new ArrayList<>();
            for (Long studentId : studentIds) {
                TrainingPlanStudent tps = new TrainingPlanStudent();
                tps.setPlanId(planId);
                tps.setUserId(studentId);
                tps.setEnrolledAt(LocalDateTime.now());
                planStudents.add(tps);
            }
            trainingPlanStudentMapper.batchInsert(planStudents);
        }
    }

    /**
     * 辅助方法：校验对已开始计划的赛事修改是否合法
     */
    private void validateContestChanges(List<Long> oldContestIds, List<Long> newContestIds) {

        // 1. 如果新旧列表相同，则无需校验
        if (oldContestIds == null || oldContestIds.equals(newContestIds)) {
            return;
        }

        // 2. 在内存中计算出被移除的赛事ID
        Set<Long> newContestIdSet = newContestIds.stream().collect(Collectors.toSet());
        List<Long> removedContestIds = oldContestIds.stream()
                .filter(id -> !newContestIdSet.contains(id))
                .collect(Collectors.toList());

        // 如果没有赛事被移除，则无需校验
        if (CollectionUtils.isEmpty(removedContestIds)) {
            return;
        }

        // 3. 一次性查询所有被移除赛事的信息 (1次查询)
        List<LocalDateTime> removedContestsST = contestMapper.getStartTimeListByIds(removedContestIds);

        // 4. 在内存中检查是否有任何一个已开始或已结束的赛事被移除
        LocalDateTime now = LocalDateTime.now();
        for (LocalDateTime startTime : removedContestsST) {
            if (now.isAfter(startTime)) {
                throw new BusinessException("只能移除未开始的赛事");
            }
        }
    }
}

