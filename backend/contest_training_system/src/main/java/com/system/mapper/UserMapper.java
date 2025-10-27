package com.system.mapper;

import com.system.dto.UserQueryDTO;
import com.system.entity.User;
import com.system.vo.UserProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户模块的数据访问层接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户实体，如果不存在则返回 null
     */
    User findByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户实体，如果不存在则返回 null
     */
    User findByPhoneNumber(@Param("phoneNumber") String phone);

    /**
     * 根据学号查询用户
     *
     * @param studentId 学号
     * @return 用户实体，如果不存在则返回 null
     */
    User findByStudentId(@Param("studentId") String studentId);

    /**
     * 根据邮箱或手机号查询用户
     *
     * @param identifier 邮箱或手机号
     * @return 用户实体
     */
    User findByEmailOrPhone(@Param("identifier") String identifier);

    /**
     * 获取用户的主要信息(UserProfileVO)
     *
     * @Param userId 用户ID
     * @return： 用户主要信息视图对象
     */
    User findBasicInfoById(@Param("userId") Long userId);

    /**
     * 插入一个新用户
     *
     * @param user 用户实体
     * @return 影响的行数
     */
    int insertUser(User user);

    /**
     * 根据用户ID查询用户（用于获取个人信息）
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    User findById(@Param("userId") Long userId);

    /**
     * 更新用户信息
     *
     * @param user 包含需要更新信息的用户实体
     * @return 影响的行数
     */
    int updateUser(User user);

    /**
     *  删除用户（逻辑删除，将状态设为非激活）
     * @param userId
     * @return
     */
    int deactivateUserById(@Param("userId") Long userId);


    /**
     * 分页查询用户列表 (优化版)
     *
     * @param queryDTO      查询条件 DTO, 在XML中命名为 "query"
     * @param sortColumn    排序列名 (安全)
     * @param sortDirection 排序方向 (安全)
     * @return 分页后的用户列表
     */
    List<UserProfileVO> pageQuery(
            @Param("query") UserQueryDTO queryDTO,
            @Param("sortColumn") String sortColumn,
            @Param("sortDirection") String sortDirection
    );
}