package com.tradesys.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
public class SecurityUtils {

    private SecurityUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 获取当前认证对象
     *
     * @return Authentication对象，如果未登录则返回null
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，如果未登录则返回null
     */
    public static String getUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal != null) {
            return principal.toString();
        }
        
        return null;
    }

    /**
     * 获取当前登录用户详情
     *
     * @return UserDetails对象，如果未登录则返回null
     */
    public static UserDetails getUserDetails() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        
        return null;
    }

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限集合，如果未登录则返回null
     */
    public static Collection<? extends GrantedAuthority> getAuthorities() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities();
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param role 角色名称（不包含ROLE_前缀）
     * @return true 拥有该角色, false 不拥有
     */
    public static boolean hasRole(String role) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        if (authorities == null) {
            return false;
        }
        
        String roleWithPrefix = "ROLE_" + role;
        for (GrantedAuthority authority : authorities) {
            if (roleWithPrefix.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否是超级管理员
     *
     * @return true 是超级管理员, false 不是
     */
    public static boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * 检查当前用户是否是管理员
     *
     * @return true 是管理员, false 不是
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("SUPER_ADMIN");
    }

    /**
     * 检查当前用户是否是代理商
     *
     * @return true 是代理商, false 不是
     */
    public static boolean isAgent() {
        return hasRole("AGENT");
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true 已登录, false 未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
