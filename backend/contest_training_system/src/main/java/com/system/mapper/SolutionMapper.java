package com.system.mapper;

import com.system.entity.SolutionReport;
import com.system.vo.SolutionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SolutionMapper {
    void insert(SolutionReport solutionReport);
    
    List<SolutionVO> findByProblemId(@Param("problemId") Long problemId);
    
    SolutionReport findById(@Param("reportId") Long reportId);

    /**
     * 更新题解
     */
    void update(SolutionReport solutionReport);

    /**
     * 删除题解
     */
    void deleteById(@Param("reportId") Long reportId);

    /**
     * 获取题解详情（包含作者信息）
     */
    SolutionVO findDetailById(@Param("reportId") Long reportId);
}
