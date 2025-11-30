package com.system.controller;

import com.system.dto.UserLoginDTO;
import com.system.dto.UserQueryDTO;
import com.system.dto.UserRegisterDTO;
import com.system.dto.UserUpdateDTO;
import com.system.service.UserService;
import com.system.vo.LoginSuccessVO;
import com.system.vo.PageResultVO;
import com.system.vo.Result;
import com.system.vo.UserProfileVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理模块的Controller
 */
@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 1.1 注册用户
     */
    @PostMapping("/register")
    public Result studentRegister(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.studentRegister(userRegisterDTO);
        return Result.success();
    }

    /**
     * 1.2 用户登录
     */
    @PostMapping("/login")
    public Result<LoginSuccessVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        LoginSuccessVO loginSuccessVO = userService.login(userLoginDTO);
        return Result.success(loginSuccessVO);
    }

    /**
     * 1.3 注销用户
     */
    @PostMapping("/user/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result deactivateUser() {
        userService.deactivateCurrentUser();
        return Result.success();
    }

    /**
     * 1.4 更新用户信息
     */
    @PutMapping("/user/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result updateUserProfile(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserProfile(userUpdateDTO);
        return Result.success();
    }

    /**
     * 1.5 获取用户信息
     */
    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result<UserProfileVO> getUserProfile() {
        UserProfileVO userProfile = userService.getUserProfile();
        return Result.success(userProfile);
    }

    /**
     * 1.6 用户列表分页查询（仅限管理员）
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResultVO> getUserList(UserQueryDTO userQueryDTO) {

        PageResultVO<UserProfileVO> pageResult = userService.getUserList(userQueryDTO);
        return Result.success(pageResult);
    }
}