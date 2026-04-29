package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysUser;
import com.tradesys.service.SysRoleService;
import com.tradesys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户Controller
 *
 * @author TradeSys Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    /**
     * 分页查询用户列表
     *
     * @param pageQuery 分页参数
     * @param user      查询条件
     * @return 用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<List<SysUser>> list(PageQuery pageQuery, SysUser user) {
        return sysUserService.queryPage(pageQuery, user);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<SysUser> getById(@PathVariable Long id) {
        return sysUserService.getDetailById(id);
    }

    /**
     * 新增用户
     *
     * @param user      用户信息
     * @param roleIds   角色ID列表
     * @return 操作结果
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Void> add(@RequestBody SysUser user,
                           @RequestParam(required = false) List<Long> roleIds) {
        return sysUserService.saveWithRoles(user, roleIds);
    }

    /**
     * 更新用户
     *
     * @param user      用户信息
     * @param roleIds   角色ID列表
     * @return 操作结果
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Void> update(@RequestBody SysUser user,
                              @RequestParam(required = false) List<Long> roleIds) {
        return sysUserService.updateWithRoles(user, roleIds);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return sysUserService.deleteWithRoles(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/delete/batch")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        return sysUserService.deleteByIds(ids);
    }

    /**
     * 重置密码
     *
     * @param id          用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Void> resetPassword(@RequestParam Long id,
                                      @RequestParam String newPassword) {
        return sysUserService.resetPassword(id, newPassword);
    }

    /**
     * 检查用户名是否唯一
     *
     * @param username  用户名
     * @param excludeId 排除的用户ID
     * @return 检查结果
     */
    @GetMapping("/check-username")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public Result<Boolean> checkUsername(@RequestParam String username,
                                         @RequestParam(required = false) Long excludeId) {
        boolean unique = sysUserService.checkUsernameUnique(username, excludeId);
        return Result.success(unique);
    }

    /**
     * 获取所有启用的用户
     *
     * @return 用户列表
     */
    @GetMapping("/all-active")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','AGENT')")
    public Result<List<SysUser>> getAllActive() {
        return sysUserService.listAllActive();
    }
}
