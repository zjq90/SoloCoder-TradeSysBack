package com.tradesys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.AgentService;
import com.tradesys.service.SysRoleService;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final AgentService agentService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "系统用户管理");
        model.addAttribute("activeMenu", "system-user");
        return "system/user";
    }

    @GetMapping("/api/current-permissions")
    @ResponseBody
    public Result<List<String>> getCurrentUserPermissions() {
        String username = SecurityUtils.getUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<String> permissions = sysUserService.getUserPermissions(user.getId());
        return Result.success(permissions);
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
    public Result<List<SysUser>> list(PageQuery pageQuery, SysUser user) {
        Page<SysUser> page = sysUserService.queryPage(pageQuery, user);
        return Result.success(page.getRecords(), page.getTotal());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getDetailById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/api/form-data")
    @ResponseBody
    public Result<Map<String, Object>> formData() {
        Map<String, Object> data = new HashMap<>();
        List<SysRole> roles = sysRoleService.listAllEnabled();
        List<Agent> agents = agentService.getTree() != null && agentService.getTree().getData() != null 
            ? agentService.getTree().getData() 
            : new ArrayList<>();
        data.put("roles", roles);
        data.put("agents", agents);
        return Result.success(data);
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Result<Void> add(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String realName = (String) params.get("realName");
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 1;
        Object agentIdObj = params.get("agentId");
        Long agentId = null;
        if (agentIdObj != null) {
            if (agentIdObj instanceof Number) {
                agentId = ((Number) agentIdObj).longValue();
            } else if (agentIdObj instanceof String) {
                try {
                    agentId = Long.parseLong((String) agentIdObj);
                } catch (NumberFormatException e) {
                    agentId = null;
                }
            }
        }
        @SuppressWarnings("unchecked")
        List<Object> roleIdsObj = (List<Object>) params.get("roleIds");
        List<Long> roleIds = new ArrayList<>();
        if (roleIdsObj != null) {
            for (Object obj : roleIdsObj) {
                if (obj instanceof Number) {
                    roleIds.add(((Number) obj).longValue());
                }
            }
        }

        if (!sysUserService.checkUsernameUnique(username, null)) {
            return Result.error("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(status);
        user.setAgentId(agentId);

        boolean result = sysUserService.saveWithRoles(user, roleIds);
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

        SysUser exist = sysUserService.getById(id);
        if (exist == null) {
            return Result.error("用户不存在");
        }

        String username = (String) params.get("username");
        if (username != null && !sysUserService.checkUsernameUnique(username, id)) {
            return Result.error("用户名已存在");
        }

        String realName = (String) params.get("realName");
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : null;
        Object agentIdObj = params.get("agentId");
        Long agentId = null;
        if (agentIdObj != null) {
            if (agentIdObj instanceof Number) {
                agentId = ((Number) agentIdObj).longValue();
            } else if (agentIdObj instanceof String) {
                try {
                    agentId = Long.parseLong((String) agentIdObj);
                } catch (NumberFormatException e) {
                    agentId = null;
                }
            }
        }
        @SuppressWarnings("unchecked")
        List<Object> roleIdsObj = (List<Object>) params.get("roleIds");
        List<Long> roleIds = new ArrayList<>();
        if (roleIdsObj != null) {
            for (Object obj : roleIdsObj) {
                if (obj instanceof Number) {
                    roleIds.add(((Number) obj).longValue());
                }
            }
        }

        SysUser user = new SysUser();
        user.setId(id);
        if (username != null) user.setUsername(username);
        if (realName != null) user.setRealName(realName);
        if (phone != null) user.setPhone(phone);
        if (email != null) user.setEmail(email);
        if (status != null) user.setStatus(status);
        if (params.containsKey("agentId")) user.setAgentId(agentId);

        boolean result = sysUserService.updateWithRoles(user, roleIds);
        if (result) {
            return Result.ok();
        }
        return Result.error("更新失败");
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        boolean result = sysUserService.deleteWithRoles(id);
        if (result) {
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    @PostMapping("/api/reset-password/{id}")
    @ResponseBody
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error("密码长度至少6位");
        }
        boolean result = sysUserService.resetPassword(id, newPassword);
        if (result) {
            return Result.ok();
        }
        return Result.error("重置密码失败");
    }

    @PostMapping("/api/assign-roles")
    @ResponseBody
    public Result<Void> assignRoles(@RequestBody Map<String, Object> params) {
        Long userId = params.get("userId") != null ? ((Number) params.get("userId")).longValue() : null;
        if (userId == null) {
            return Result.error("参数错误");
        }
        @SuppressWarnings("unchecked")
        List<Long> roleIds = (List<Long>) params.get("roleIds");
        
        sysUserService.assignRoles(userId, roleIds);
        return Result.ok();
    }
}
