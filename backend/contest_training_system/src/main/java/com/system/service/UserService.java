package com.system.service;

import com.system.dto.UserQueryDTO;
import com.system.dto.UserRegisterDTO;
import com.system.dto.UserLoginDTO;
import com.system.dto.UserUpdateDTO;
import com.system.vo.LoginSuccessVO;
import com.system.vo.PageResultVO;
import com.system.vo.UserProfileVO;

/**
 * 用户模块的业务逻辑层接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param userRegisterDTO 注册信息
     */
    void studentRegister(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     * @param userLoginDTO 登录信息
     * @return 包含Token的VO对象
     */
    LoginSuccessVO login(UserLoginDTO userLoginDTO);

    /**
     * 获取当前登录用户的详细信息
     * @return 用户信息的VO对象
     */
    UserProfileVO getUserProfile();

    /**
     * 更新当前登录用户的个人信息
     * @param userUpdateDTO 包含更新信息的DTO对象
     */
    void updateUserProfile(UserUpdateDTO userUpdateDTO);

    /**
     * 注销当前登录用户
     */
    void deactivateCurrentUser();

    /**
     * 用户列表分页查询
     */
     PageResultVO<UserProfileVO> getUserList(UserQueryDTO userQueryDTO);
}