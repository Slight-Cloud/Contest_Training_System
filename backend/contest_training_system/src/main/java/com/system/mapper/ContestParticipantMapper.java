package com.system.mapper;

import com.system.entity.ContestParticipant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContestParticipantMapper {

    /**
     * 插入参赛记录
     * @param contestParticipant 参赛信息
     */
    void insert(ContestParticipant contestParticipant);

    /**
     * 根据赛事ID和用户ID查询参赛记录
     * @param contestId 赛事ID
     * @param userId 用户ID
     * @return 参赛信息
     */
    ContestParticipant findByContestAndUser(@Param("contestId") Long contestId, @Param("userId") Long userId);

    /**
     * 根据赛事ID统计参赛人数
     * @param contestId 赛事ID
     * @return 人数
     */
    long countByContestId(Long contestId);

    /**
     * 根据赛事ID删除所有参赛者
     * @param contestId 赛事ID
     */
    @Delete("DELETE FROM contest_participant WHERE contest_id = #{contestId}")
    void deleteByContestId(Long contestId);



}
