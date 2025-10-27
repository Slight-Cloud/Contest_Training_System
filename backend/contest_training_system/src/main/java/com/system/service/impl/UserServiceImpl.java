package com.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.system.dto.UserLoginDTO;
import com.system.dto.UserQueryDTO;
import com.system.dto.UserRegisterDTO;
import com.system.dto.UserUpdateDTO;
import com.system.entity.SortOrder;
import com.system.entity.User;
import com.system.exception.BusinessException;
import com.system.mapper.UserMapper;
import com.system.service.UserService;
import com.system.util.JwtUtil;
import com.system.util.UserContext;
import com.system.vo.LoginSuccessVO;
import com.system.vo.PageResultVO;
import com.system.vo.UserProfileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 注册涉及多步操作，保证一致性
    public void studentRegister(UserRegisterDTO userRegisterDTO) {

        //0. 检查手机号是否已被注册
        if (userMapper.findByPhoneNumber(userRegisterDTO.getPhoneNumber()) != null) {
            throw new BusinessException("该手机号已被注册");
        }

        // 1. 检查邮箱是否已被注册
        if (userMapper.findByEmail(userRegisterDTO.getEmail()) != null) {
            throw new BusinessException("该邮箱已被注册");
        }

        // 2. 检查学号是否已被注册
        if (userMapper.findByStudentId(userRegisterDTO.getStudentId()) != null) {
            throw new BusinessException("该学号已被注册");
        }

        // 3. 创建用户实体并加密密码
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setPasswordHash(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole("STUDENT"); // 默认角色为学生

        // 4. 插入数据库
        userMapper.insertUser(user);
    }

    @Override
    public LoginSuccessVO login(UserLoginDTO userLoginDTO) {
        // 1. 根据邮箱或手机号查询用户
        User user = userMapper.findByEmailOrPhone(userLoginDTO.getEmailOrPhone());

        // 2. 检查用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPasswordHash()) &&
                !userLoginDTO.getPassword().equals(user.getPasswordHash())) { // 允许明文密码登录（仅限测试环境）
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成JWT
        String token = JwtUtil.generateToken(user.getUserId(), user.getRole());

        // 5. 返回Token
        return new LoginSuccessVO(token);
    }

    @Override
    public UserProfileVO getUserProfile() {
        // 从UserContext中获取当前用户的ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录或Token无效");
        }

        // 将User实体转换为UserProfileVO
        User user = userMapper.findBasicInfoById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        BeanUtils.copyProperties(user, vo);

        return vo;
    }

    @Override
    public void updateUserProfile(UserUpdateDTO userUpdateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录或Token无效");
        }

        User userToUpdate = new User();
        userToUpdate.setUserId(userId); // 设置要更新的用户ID
        BeanUtils.copyProperties(userUpdateDTO, userToUpdate);

        // 如果传入了新密码，则进行加密
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            userToUpdate.setPasswordHash(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        // 调用mapper进行更新，MyBatis的动态SQL会处理非null字段
        userMapper.updateUser(userToUpdate);
    }

    @Override
    public void deactivateCurrentUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录或Token无效");
        }
        userMapper.deactivateUserById(userId);
    }

    @Override
    public PageResultVO<UserProfileVO> getUserList(UserQueryDTO userQueryDTO) {

        // 解析前端的 sortBy 参数
        SortOrder sortOrder = SortOrder.forUser(userQueryDTO.getSortBy());
        String sortColumn = "u." + sortOrder.getColumn(); // 加上表别名前缀 u.
        String sortDirection = sortOrder.getDirection();

        // 开启分页
        PageHelper.startPage(userQueryDTO.getPage(), userQueryDTO.getPageSize());

        // 优化3: 执行查询，传入所有准备好的参数
        Page<UserProfileVO> page = (Page<UserProfileVO>) userMapper.pageQuery(
                userQueryDTO,
                sortColumn,
                sortDirection
        );

        // 封装分页结果
        return new PageResultVO<>(page.getTotal(), page.getPageNum(), page.getPageSize(), page.getResult());
    }

}