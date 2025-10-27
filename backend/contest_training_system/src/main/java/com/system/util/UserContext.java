package com.system.util;

/**
 * 使用ThreadLocal存储当前登录用户的ID和Role
 * 这样可以在Service层或任何地方方便地获取，而不需要层层传递参数
 */
public class UserContext {

    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>(); // 新增角色存储

    public static void setUserId(Long userId) {
        userHolder.set(userId);
    }

    public static Long getUserId() {
        return userHolder.get();
    }

    public static void setRole(String role) {
        roleHolder.set(role);
    }

    public static String getRole() {
        return roleHolder.get();
    }

    public static void clear() {
        userHolder.remove();
        roleHolder.remove();
    }
}
