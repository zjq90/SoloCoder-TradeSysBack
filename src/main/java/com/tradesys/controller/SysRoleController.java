package com.tradesys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.SysPermission;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.SysPermissionService;
import com.tradesys.service.SysRoleService;
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
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;
    private final SysUserService sysUserService;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "角色管理");
        model.addAttribute("activeMenu", "system-role");
        return "system/role";
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

    @GetMapping("/api/list")
    @ResponseBody
    public Result<List<SysRole>> list(PageQuery pageQuery, SysRole role) {
        Page<SysRole> page = sysRoleService.queryPage(pageQuery, role);
        return Result.success(page.getRecords(), page.getTotal());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getDetailById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @GetMapping("/api/all")
    @ResponseBody
    public Result<List<SysRole>> all() {
        List<SysRole> roles = sysRoleService.listAllEnabled();
        return Result.success(roles);
    }

    @GetMapping("/api/form-data")
    @ResponseBody
    public Result<Map<String, Object>> formData() {
        Map<String, Object> data = new HashMap<>();
        List<SysPermission> permissions = sysPermissionService.treeList();
        data.put("permissions", permissions);
        return Result.success(data);
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        String roleName = (String) params.get("roleName");
        String roleCode = (String) params.get("roleCode");
        String description = (String) params.get("description");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 1;
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");

        if (!sysRoleService.checkRoleCodeUnique(roleCode, null)) {
            return Result.error("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleName(roleName);
        role.setRoleCode(roleCode);
        role.setDescription(description);
        role.setStatus(status);

        boolean result = sysRoleService.saveWithPermissions(role, permissionIds);
        if (result) {
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @PostMapping("/api/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? ((Number) params.get("id")).longValue() : null;
        if (id == null) {
            return Result.error("参数错误");
        }

        SysRole exist = sysRoleService.getById(id);
        if (exist == null) {
            return Result.error("角色不存在");
        }

        String roleCode = (String) params.get("roleCode");
        if (roleCode != null && !sysRoleService.checkRoleCodeUnique(roleCode, id)) {
            return Result.error("角色编码已存在");
        }

        String roleName = (String) params.get("roleName");
        String description = (String) params.get("description");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");

        SysRole role = new SysRole();
        role.setId(id);
        if (roleName != null) role.setRoleName(roleName);
        if (roleCode != null) role.setRoleCode(roleCode);
        if (description != null) role.setDescription(description);
        if (status != null) role.setStatus(status);

        boolean result = sysRoleService.updateWithPermissions(role, permissionIds);
        if (result) {
            return Result.ok();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        boolean result = sysRoleService.deleteWithPermissions(id);
        if (result) {
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    @PostMapping("/api/assign-permissions")
    @ResponseBody
    public Result<Void> assignPermissions(@RequestBody Map<String, Object> params) {
        Long roleId = params.get("roleId") != null ? ((Number) params.get("roleId")).longValue() : null;
        if (roleId == null) {
            return Result.error("参数错误");
        }
        @SuppressWarnings("unchecked")
        List<Long> permissionIds = (List<Long>) params.get("permissionIds");
        
        sysRoleService.assignPermissions(roleId, permissionIds);
        return Result.ok();
    }
}
