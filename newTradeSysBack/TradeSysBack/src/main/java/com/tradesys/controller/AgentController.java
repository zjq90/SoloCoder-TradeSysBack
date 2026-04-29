package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.AgentService;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final SysUserService sysUserService;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "代理商管理");
        model.addAttribute("activeMenu", "agent");
        return "agent";
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
    public Result<List<Agent>> list(PageQuery pageQuery, Agent agent) {
        return agentService.getPage(pageQuery, agent);
    }

    @GetMapping("/api/tree")
    @ResponseBody
    public Result<List<Agent>> tree() {
        return agentService.getTree();
    }

    @GetMapping("/api/children/{parentId}")
    @ResponseBody
    public Result<List<Agent>> children(@PathVariable Long parentId) {
        return agentService.getChildren(parentId);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<Agent> getById(@PathVariable Long id) {
        return agentService.getDetail(id);
    }

    @GetMapping("/api/stats/{agentId}")
    @ResponseBody
    public Result<Map<String, Object>> stats(@PathVariable Long agentId) {
        return agentService.getStats(agentId);
    }

    @GetMapping("/api/options")
    @ResponseBody
    public Result<List<Map<String, Object>>> options() {
        return agentService.getAgentOptions();
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Result<Void> add(@RequestBody Agent agent) {
        return agentService.addAgent(agent);
    }

    @PostMapping("/api/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Agent agent) {
        return agentService.updateAgent(agent);
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        return agentService.deleteAgent(id);
    }
}
