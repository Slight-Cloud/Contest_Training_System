package com.system.config;

import com.system.util.JwtUtil;
import com.system.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final Long userId;
        final String role;

        // 如果没有Authorization头或者不是以"Bearer "开头，则直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // "Bearer " 后面的部分
        try {
            // 检查token是否过期
            if (JwtUtil.isExpired(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            userId = JwtUtil.getUserId(jwt);
            role = JwtUtil.getRole(jwt); // 解析角色
        } catch (Exception e) {
            // Token解析失败，直接放行，后续Spring Security会处理认证失败
            filterChain.doFilter(request, response);
            return;
        }

        // 如果成功解析出用户ID，并且当前安全上下文中没有认证信息
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 将用户ID存入UserContext
            UserContext.setUserId(userId);
            UserContext.setRole(role); // 存储角色

            // Spring Security需要一个 GrantedAuthority 列表来表示权限
            // 我们在角色名前加上 "ROLE_" 前缀，这是Spring Security的约定
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));//

            // 创建一个已认证的Authentication对象，并设置到Spring Security的上下文中
            // 这里的principal是userId，我们没有查询数据库获取UserDetails，因为对于已验证的JWT来说不是必须的
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, // Principal可以是用户ID，也可以是UserDetails对象
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 执行过滤器链的下一个过滤器
        filterChain.doFilter(request, response);

        // 在请求结束后，清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}