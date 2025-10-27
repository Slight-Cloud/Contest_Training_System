package com.system.mapper;

import com.github.pagehelper.Page;
import com.system.dto.ContestQueryDTO;
import com.system.entity.Contest;
import com.system.vo.ContestListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ContestMapper {

    /**
     * 插入赛事信息
     *
     * @param contest 赛事对象
     */
    void insert(Contest contest);

    /**
     * 根据ID查询赛事
     *
     * @param contestId 赛事ID
     * @return 赛事对象
     */
    Contest getById(Long contestId);

    /**
     * 根据条件分页查询赛事列表
     *
     * @param contestQueryDTO 查询条件
     * @param sortColumn
     * @param sortDirection
     * @return 赛事列表
     */
    List<ContestListVO> pageQuery(
            @Param("query") ContestQueryDTO contestQueryDTO, // 将 DTO 命名为 "query"
            @Param("sortColumn") String sortColumn,           // 命名为 "sortColumn"
            @Param("sortDirection") String sortDirection,  // 命名为 "sortDirection"
            @Param("now") LocalDateTime now             // 将时间命名为 "now"
    );

    /**
     * 更新赛事信息
     *
     * @param contest 赛事对象
     */
    void update(Contest contest);

    /**
     * 根据ID删除赛事
     *
     * @param contestId 赛事ID
     */
    void deleteById(Long contestId);

    /**
     * 根据标题查询赛事，用于检查标题唯一性
     *
     * @param title 赛事标题
     * @return 赛事对象
     */
    Contest getByTitle(String title);

    /**
     * 根据多个ID查询赛事列表
     *
     * @param idsList
     * @return
     */
    List<LocalDateTime> getStartTimeListByIds(List<Long> idsList);

    @Update("update contest set state = 'HIDDEN' where contest_id = #{contestId}")
    void deactivateContestById(Long contestId);
}
