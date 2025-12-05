package com.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.system.dto.ContestCreateDTO;
import com.system.dto.ContestJoinDTO;
import com.system.dto.ContestQueryDTO;
import com.system.dto.ContestUpdateDTO;
import com.system.entity.Contest;
import com.system.entity.ContestParticipant;
import com.system.entity.ContestProblem;
import com.system.entity.SortOrder;
import com.system.exception.BusinessException;
import com.system.mapper.ContestMapper;
import com.system.mapper.ContestParticipantMapper;
import com.system.mapper.ContestProblemMapper;
import com.system.service.ContestService;
import com.system.util.UserContext;
import com.system.vo.ContestDetailVO;
import com.system.vo.ContestListVO;
import com.system.vo.PageResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ContestServiceImpl implements ContestService {

    private final ContestMapper contestMapper;
    private final ContestProblemMapper contestProblemMapper;
    private final ContestParticipantMapper contestParticipantMapper;
    private final PasswordEncoder passwordEncoder; // 注入用于密码加密

    public ContestServiceImpl(ContestMapper contestMapper, ContestProblemMapper contestProblemMapper, ContestParticipantMapper contestParticipantMapper, PasswordEncoder passwordEncoder) {
        this.contestMapper = contestMapper;
        this.contestProblemMapper = contestProblemMapper;
        this.contestParticipantMapper = contestParticipantMapper;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    @Transactional // 开启事务
    public Long createContest(ContestCreateDTO contestCreateDTO) {

        //1.不必检查title唯一性，已全局异常处理

        // 2. 拷贝属性并设置默认值
        Contest contest = new Contest();
        BeanUtils.copyProperties(contestCreateDTO, contest);

        contest.setCreatorId(UserContext.getUserId());
        contest.setState("USING"); // 初始为使用
        contest.setCreatedAt(LocalDateTime.now());
        contest.setUpdatedAt(LocalDateTime.now());

        // 如果设置了密码，则进行加密
        if (contestCreateDTO.getPassword() != null && !contestCreateDTO.getPassword().isEmpty()) {
            contest.setPasswordHash(passwordEncoder.encode(contestCreateDTO.getPassword()));
        }

        // 3. 插入Contest表，获取生成的contestId
        contestMapper.insert(contest);
        Long contestId = contest.getContestId();

        // 4. 处理题目列表，批量插入ContestProblem表
        List<Long> problemIds = contestCreateDTO.getProblemIds();
        if (!CollectionUtils.isEmpty(problemIds)) {
            List<ContestProblem> contestProblems = new ArrayList<>();
            for (int i = 0; i < problemIds.size(); i++) {
                ContestProblem contestProblem = new ContestProblem();
                contestProblem.setContestId(contestId);
                contestProblem.setProblemId(problemIds.get(i));
                contestProblem.setDisplayOrder(i + 1); // 设置题目顺序
                contestProblems.add(contestProblem);
            }
            contestProblemMapper.batchInsert(contestProblems);
        }

        return contestId;
    }

    @Override
    @Transactional
    public void updateContest(ContestUpdateDTO contestUpdateDTO) {
        // 1. 检查赛事是否存在
        Contest existingContest = contestMapper.getById(contestUpdateDTO.getContestId());
        if (existingContest == null) {
            throw new BusinessException("赛事不存在或已被删除");
        }

        // 2. 只允许修改未开始的赛事
        LocalDateTime now = LocalDateTime.now();
        if ("USING".equals(existingContest.getState()) && !now.isBefore(existingContest.getStartTime())) {
            throw new BusinessException("只有未开始的赛事允许修改");
        }

        // 3. 拷贝属性并设置更新时间
        BeanUtils.copyProperties(contestUpdateDTO, existingContest);
        existingContest.setUpdatedAt(now);

        // 4. 如果更新了密码，则重新加密
        if (contestUpdateDTO.getPassword() != null) {
            existingContest.setPasswordHash(contestUpdateDTO.getPassword().isEmpty() ? null : passwordEncoder.encode(contestUpdateDTO.getPassword()));
        }

        // 5. 更新Contest表
        contestMapper.update(existingContest);

        // 6. 更新题目列表（先删除旧的，再插入新的）
        if (contestUpdateDTO.getProblemIds() != null) {
            contestProblemMapper.deleteByContestId(existingContest.getContestId());
            List<Long> problemIds = contestUpdateDTO.getProblemIds();
            if (!problemIds.isEmpty()) {
                List<ContestProblem> contestProblems = new ArrayList<>();
                for (int i = 0; i < problemIds.size(); i++) {
                    ContestProblem contestProblem = new ContestProblem();
                    contestProblem.setContestId(existingContest.getContestId());
                    contestProblem.setProblemId(problemIds.get(i));
                    contestProblem.setDisplayOrder(i + 1);
                    contestProblems.add(contestProblem);
                }
                contestProblemMapper.batchInsert(contestProblems);
            }
        }
    }

    @Override
    @Transactional
    public void deleteContest(Long contestId) {
        // 检查赛事是否存在
        Contest contest = contestMapper.getById(contestId);
        if (contest == null) {
            throw new BusinessException("赛事不存在或已被删除");
        }

        //如果赛事还未开始则直接删除，如果已结束则不再显示，进行中的赛事不可删除。（使用实时计算）
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(contest.getStartTime()) && now.isBefore(contest.getEndTime())) {
            throw new BusinessException("进行中的赛事不可删除");
        }
        else if (now.isAfter(contest.getEndTime())) {

            //对已经结束的赛事进行逻辑删除，标记为隐藏
            contestMapper.deactivateContestById(contestId);
            contest.setState("HIDDEN");
            contest.setUpdatedAt(LocalDateTime.now());
        }
        else {

            // 删除ContestProblem关联关系
            contestProblemMapper.deleteByContestId(contestId);

            // 删除ContestParticipant报名记录
            contestParticipantMapper.deleteByContestId(contestId);

            // 最后删除Contest表记录
            contestMapper.deleteById(contestId);
        }

    }

    @Override
    public PageResultVO getContestList(ContestQueryDTO contestQueryDTO) {

        //开启分页
        PageHelper.startPage(contestQueryDTO.getPage(), contestQueryDTO.getPageSize());

        //准备参数

        //普通用户只能查看使用中的赛事
        String role = UserContext.getRole();

        if (!("ADMIN".equals(role) || "TEACHER".equals(role))) {
            contestQueryDTO.setState("USING");
        }

        //处理排序结果: 从枚举获取安全的列名和排序方向
        SortOrder sortOrder = SortOrder.forContest(contestQueryDTO.getSortBy());
        String sortColumn = sortOrder.getColumn();
        String sortDirection = sortOrder.getDirection();
        LocalDateTime now = LocalDateTime.now();

        Page<ContestListVO> page = (Page<ContestListVO>) contestMapper.pageQuery(contestQueryDTO, sortColumn, sortDirection, now);

        return new PageResultVO(page.getTotal(), page.getPageNum(), page.getPageSize(), page);
    }

    @Override
    public ContestDetailVO getContestDetail(Long contestId, String password) {
        Contest contest = contestMapper.getById(contestId);
        if (contest == null||(UserContext.getRole().equals("STUDENT")&&contest.getState().equals("HIDDEN"))) {
            throw new BusinessException("赛事不存在或已隐藏");
        }

        String role = UserContext.getRole();
        Long currentUserId = UserContext.getUserId();
        LocalDateTime now = LocalDateTime.now();
        boolean isAdmin = "ADMIN".equals(role);
        boolean isCreator = contest.getCreatorId() != null && contest.getCreatorId().equals(currentUserId);
        boolean isPrivate = "PRIVATE".equals(contest.getVisibility());
        boolean isScheduled = contest.getStartTime().isAfter(now);

        // 逻辑0: 未开始的私密赛事访问控制（管理员和创建者除外）
        if (isPrivate && isScheduled && !isAdmin && !isCreator) {
            throw new BusinessException("当前赛事未开始");
        }

        // 逻辑1: 私有赛事详情访问需要密码验证（管理员、创建者、已参加者除外）
        // 只有在非管理员、非创建者的情况下才需要检查参赛状态和密码
        if (isPrivate && !isAdmin && !isCreator) {
            boolean hasJoined = hasJoinedContest(contestId, currentUserId);
            if (!hasJoined) {
                // 未参加的用户需要验证密码
                if (password == null || password.isEmpty()) {
                    throw new BusinessException("需要输入正确的赛事密码才能查看详情");
                }
                if (!passwordEncoder.matches(password, contest.getPasswordHash()) && !password.equals(contest.getPasswordHash())) {
                    throw new BusinessException("赛事密码错误");
                }
            }
        }

        ContestDetailVO detailVO = new ContestDetailVO();
        BeanUtils.copyProperties(contest, detailVO);

        // 动态更新状态
        if (detailVO.getEndTime().isBefore(now)) {
            detailVO.setStatus("ENDED");
        }
        else if (detailVO.getStartTime().isAfter(now)) {
            detailVO.setStatus("SCHEDULED");
        }
        else {
            detailVO.setStatus("ONGOING");
        }

        // 查询题目列表
        List<ContestDetailVO.ProblemInContestVO> problems = contestProblemMapper.findProblemsByContestId(contestId);
        detailVO.setProblems(problems);

        // 查询参赛人数
        long participantCount = contestParticipantMapper.countByContestId(contestId);
        detailVO.setParticipantCount(participantCount);

        return detailVO;
    }

    @Override
    public void joinContest(ContestJoinDTO contestJoinDTO) {
        Long contestId = contestJoinDTO.getContestId();
        Long userId = UserContext.getUserId();

        // 1. 检查赛事是否存在
        Contest contest = contestMapper.getById(contestId);
        if (contest == null) {
            throw new BusinessException("赛事不存在或已被删除");
        }

        // 2. 检查是否在报名时间
        if (LocalDateTime.now().isAfter(contest.getStartTime())) {
            throw new BusinessException("赛事已开始，无法报名");
        }

        // 3. 检查是否已报名
        if (hasJoinedContest(contestId, userId)) {
            throw new BusinessException("你已报名该赛事");
        }

        // 4. 如果是私有赛事，验证密码
        if (Objects.equals(contest.getVisibility(), "PRIVATE")) {
            if (contestJoinDTO.getPassword() == null || !passwordEncoder.matches(contestJoinDTO.getPassword(), contest.getPasswordHash())) {
                throw new BusinessException("赛事密码错误");
            }
        }

        // 5. 插入报名记录
        ContestParticipant participant = new ContestParticipant();
        participant.setContestId(contestId);
        participant.setUserId(userId);
        participant.setTeamName(contestJoinDTO.getTeamName());
        participant.setRegisteredAt(LocalDateTime.now());
        participant.setIsOfficial(1); // 默认为正式参赛

        contestParticipantMapper.insert(participant);
    }

    @Override
    public boolean hasJoinedContest(ContestJoinDTO contestJoinDTO) {
        Long contestId = contestJoinDTO.getContestId();
        Long userId = UserContext.getUserId();
        return hasJoinedContest(contestId, userId);
    }


    public boolean hasJoinedContest(Long contestId, Long userId) {
        ContestParticipant participant = contestParticipantMapper.findByContestAndUser(contestId, userId);
        return participant != null;
    }


}
