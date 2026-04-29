package com.tradesys.controller;

import com.tradesys.common.Result;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.SysPermissionService;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;
    private final SysUserService sysUserService;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "权限管理");
        model.addAttribute("activeMenu", "system-permission");
        return "system/permission";
    }

    private void addUserInfoToModel(Model model) {
        String username = SecurityUtils.getUsername();
        if (username != null) {
            SysUser user = sysUserService.getByUsername(username);
            if (user != null) {
                model.addAttribute("currentUser", user);
                List<SysRole> roles = sysUserService.getUserRoles(user.getId());
                if (roles != null && !roles.isEmpty()) {
                    model.addAttribute("currentRole", roles.get(0).getRoleName());
                }
            }
        }
    }

    @GetMapping("/api/tree")
    @ResponseBody
    public Result<List<SysPermission>> tree() {
        List<SysPermission> permissions = sysPermissionService.treeList();
        return Result.success(permissions);
    }

    @GetMapping("/api/all")
    @ResponseBody
    public Result<List<SysPermission>> all() {
        List<SysPermission> permissions = sysPermissionService.listAllEnabled();
        return Result.success(permissions);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<SysPermission> getById(@PathVariable Long id) {
        SysPermission permission = sysPermissionService.getById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    @GetMapping("/api/form-data")
    @ResponseBody
    public Result<Map<String, Object>> formData() {
        Map<String, Object> data = new HashMap<>();
        List<SysPermission> parents = sysPermissionService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getMenuType, 1)
                        .eq(SysPermission::getStatus, 1)
                        .orderByAsc(SysPermission::getSort)
        );
        
        SysPermission root = new SysPermission();
        root.setId(0L);
        root.setPermissionName("根目录");
        root.setParentId(0L);
        
        parents.add(0, root);
        data.put("parents", parents);
        return Result.success(data);
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Result<Void> add(@RequestBody SysPermission permission) {
        if (permission.getPermissionCode() != null && !permission.getPermissionCode().isEmpty()) {
            if (!sysPermissionService.checkPermissionCodeUnique(permission.getPermissionCode(), null)) {
                return Result.error("权限编码已存在");
            }
        }
        boolean result = sysPermissionService.save(permission);
        if (result) {
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @PostMapping("/api/update")
    @ResponseBody
    public Result<Void> update(@RequestBody SysPermission permission) {
        if (permission.getPermissionCode() != null && !permission.getPermissionCode().isEmpty()) {
            if (!sysPermissionService.checkPermissionCodeUnique(permission.getPermissionCode(), permission.getId())) {
                return Result.error("权限编码已存在");
            }
        }
        boolean result = sysPermissionService.updateById(permission);
        if (result) {
            return Result.ok();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        if (sysPermissionService.hasChildren(id)) {
            return Result.error("该权限下有子权限，不能直接删除");
        }
        boolean result = sysPermissionService.deleteWithChildren(id);
        if (result) {
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
