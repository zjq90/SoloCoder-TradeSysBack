package com.tradesys.config;

import com.tradesys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 * 配置安全认证、授权、CORS等
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 配置认证管理器
     *
     * @param auth 认证管理器构建器
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sysUserService).passwordEncoder(passwordEncoder);
    }

    /**
     * CORS配置
     * 允许前端跨域访问
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的来源，生产环境应配置具体域名
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        // 预检请求的有效期
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 配置HTTP安全规则
     *
     * @param http HttpSecurity
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离项目不需要）
            .csrf().disable()
            // 启用CORS
            .cors().and()
            // 会话管理（前后端分离项目使用无状态会话）
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
            // 配置请求授权规则
            .authorizeRequests()
                // 公开访问的URL
                .antMatchers("/login", "/error", "/h2-console/**").permitAll()
                // Druid监控控制台
                .antMatchers("/druid/**").permitAll()
                // API接口需要认证
                .antMatchers("/api/**").authenticated()
                // 系统管理模块权限
                .antMatchers("/system/user/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .antMatchers("/system/role/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .antMatchers("/system/permission/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // 其他请求需要认证
                .anyRequest().authenticated()
            .and()
            // 表单登录配置
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
            .and()
            // 登出配置
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            .and()
            // H2控制台需要的配置
            .headers().frameOptions().disable();

        log.info("Spring Security配置完成");
    }
}
