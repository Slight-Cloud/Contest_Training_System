package com.system.mapper;

import com.system.dto.ProblemQueryDTO;
import com.system.entity.Problem;
import com.system.vo.ProblemListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProblemMapper {

    /**
     * 插入一个新的题目记录
     *
     * @param problem 题目实体（不需要填 problemId，数据库自增；必需字段：title/description 等）
     * @return 影响的行数（成功通常为 1）
     */
    int insertProblem(Problem problem);

    /**
     * 根据 problemId 对题目进行部分更新（仅非 null 字段会被更新）
     *
     * @param problem 含 problemId 以及待更新字段的题目实体
     * @return 影响的行数（成功通常为 1；若 problemId 不存在则为 0）
     */
    int updateProblem(Problem problem);

    /**
     * 按主键查询题目信息
     *
     * @param problemId 题目ID
     * @return 题目实体；若不存在返回 null
     */
    Problem findById(@Param("problemId") Long problemId);

    /**
     * 将题目标记为隐藏（is_hidden=1）而不是删除
     *
     * @param problemId 题目ID
     * @return 影响的行数（成功通常为 1）
     */
    int hideProblemById(@Param("problemId") Long problemId);

    /**
     * 统计该题目在竞赛关联表中的使用次数（用于判断能否删除等）
     *
     * @param problemId 题目ID
     * @return 使用次数（>=0）
     */
    int countUsageInContests(@Param("problemId") Long problemId);

    /**
     * 物理删除题目记录（需确保未被使用或已满足业务规则）
     *
     * @param problemId 题目ID
     * @return 影响的行数（成功通常为 1）
     */
    int deleteProblemById(@Param("problemId") Long problemId);


    /**
     * 根据查询条件分页获取题目列表，用于题库管理
     *
     * @param queryDTO 查询参数封装对象
     * @return 符合条件的题目列表
     */
    List<ProblemListVO> pageQuery(
            @Param("query") ProblemQueryDTO queryDTO,
            @Param("sortColumn") String sortColumn,
            @Param("sortDirection") String sortDirection

    );


}