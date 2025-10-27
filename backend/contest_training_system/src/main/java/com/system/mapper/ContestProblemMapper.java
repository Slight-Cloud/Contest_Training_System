package com.system.mapper;

import com.system.entity.ContestProblem;
import com.system.vo.ContestDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContestProblemMapper {

    /**
     * 批量插入赛事和题目的关联关系
     * @param contestProblems 关联关系列表
     */
    void batchInsert(@Param("list") List<ContestProblem> contestProblems);

    /**
     * 根据赛事ID删除所有关联关系
     * @param contestId 赛事ID
     */
    void deleteByContestId(Long contestId);

    /**
     * 根据赛事ID查询其包含的所有题目信息
     * @param contestId 赛事ID
     * @return 题目信息列表
     */
    List<ContestDetailVO.ProblemInContestVO> findProblemsByContestId(Long contestId);

    /**
     * 检查某个题目是否被任何赛事使用
     * @param problemId 题目ID
     * @return 关联的赛事数量
     */
    Integer countByProblemId(Long problemId);
}
