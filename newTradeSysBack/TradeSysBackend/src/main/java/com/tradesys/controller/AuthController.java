package com.tradesys.controller;

import com.tradesys.common.Result;
import com.tradesys.entity.SysUser;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 * 处理登录、登出、获取当前用户信息等
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> getCurrentUserInfo() {
        return sysUserService.getCurrentUser();
    }

    /**
     * 获取当前用户的权限菜单
     *
     * @return 权限菜单
     */
    @GetMapping("/menus")
    public Result<Map<String, Object>> getCurrentUserMenus() {
        Result<SysUser> userResult = sysUserService.getCurrentUser();
        if (!userResult.isSuccess()) {
            return Result.error(userResult.getMessage());
        }

        SysUser user = userResult.getData();
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", user.getRoles());
        result.put("permissions", user.getPermissions());

        return Result.success(result);
    }

    /**
     * 检查登录状态
     *
     * @return 登录状态
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> checkLoginStatus() {
        Map<String, Object> result = new HashMap<>();

        if (SecurityUtils.isAuthenticated()) {
            Result<SysUser> userResult = sysUserService.getCurrentUser();
            if (userResult.isSuccess()) {
                result.put("authenticated", true);
                result.put("username", userResult.getData().getUsername());
                result.put("realName", userResult.getData().getRealName());
                return Result.success(result);
            }
        }

        result.put("authenticated", false);
        return Result.success(result);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.error("旧密码不能为空");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("新密码不能为空");
        }

        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }

        Result<SysUser> userResult = sysUserService.getCurrentUser();
        if (!userResult.isSuccess()) {
            return Result.error("获取用户信息失败");
        }

        return sysUserService.resetPassword(userResult.getData().getId(), newPassword);
    }
}
