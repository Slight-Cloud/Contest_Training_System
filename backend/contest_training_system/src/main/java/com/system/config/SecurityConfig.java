package com.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 */
@Configuration
// @EnableWebSecurity 注解：开启Spring Security的Web安全支持，并告诉Spring Boot我们将提供自定义安全配置。
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 注入自定义的JWT过滤器
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 定义密码编码器Bean，用于密码的加密和验证。
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt强哈希函数加密密码
        return new BCryptPasswordEncoder();
    }

    /**
     * 定义安全过滤器链Bean，这是Spring Security的核心配置。
     *
     * @param http HttpSecurity对象，用于构建安全配置
     * @return 配置好的SecurityFilterChain实例
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭CSRF（跨站请求伪造）保护。因为我们使用JWT，是无状态的，不需要CSRF保护。
                .csrf(csrf -> csrf.disable())

                // 2. 配置会话管理为“无状态（STATELESS）”。服务器不创建或使用HttpSession。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 配置HTTP请求的授权规则。
                .authorizeHttpRequests(auth -> auth
//                                 ===== 开发环境临时关闭鉴权，全部放行 =====
//                                .anyRequest().permitAll()
                        // 原生产配置（需要时恢复）:
                         .requestMatchers("/register", "/login").permitAll()

                        //这里不需要添加认证，在controller层通过方法注解进行权限控制


                        .anyRequest().authenticated()
                ).addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}