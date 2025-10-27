package com.system.service;

import com.system.dto.ContestCreateDTO;
import com.system.dto.ContestJoinDTO;
import com.system.dto.ContestQueryDTO;
import com.system.dto.ContestUpdateDTO;
import com.system.vo.ContestDetailVO;
import com.system.vo.PageResultVO;

public interface ContestService {

    /**
     * 创建赛事
     * @param contestCreateDTO 赛事创建数据
     * @return 赛事ID
     */
    Long createContest(ContestCreateDTO contestCreateDTO);

    /**
     * 更新赛事
     * @param contestUpdateDTO 赛事更新数据
     */
    void updateContest(ContestUpdateDTO contestUpdateDTO);

    /**
     * 删除赛事
     * @param contestId 赛事ID
     */
    void deleteContest(Long contestId);

    /**
     * 分页查询赛事
     * @param contestQueryDTO 查询条件
     * @return 分页结果
     */
    PageResultVO getContestList(ContestQueryDTO contestQueryDTO);

    /**
     * 获取赛事详情
     * @param contestId 赛事ID
     * @return 赛事详情
     */
    ContestDetailVO getContestDetail(Long contestId);

    /**
     * 学生报名参赛
     * @param contestJoinDTO 报名信息
     */
    void joinContest(ContestJoinDTO contestJoinDTO);

    boolean hasJoinedContest(ContestJoinDTO contestJoinDTO);
}
