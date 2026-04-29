package com.tradesys.controller;

import com.tradesys.common.Result;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.DashboardService;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SysUserService sysUserService;

    @GetMapping
    public String index(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "首页");
        model.addAttribute("activeMenu", "dashboard");
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/";
    }

    @GetMapping("/product")
    public String product(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "产品信息");
        model.addAttribute("activeMenu", "product");
        return "product";
    }

    @GetMapping("/agent-account")
    public String agentAccount(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "代理账户");
        model.addAttribute("activeMenu", "agent-account");
        return "agent-account";
    }

    @GetMapping("/profit")
    public String profit(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "分润信息");
        model.addAttribute("activeMenu", "profit");
        return "profit";
    }

    @GetMapping("/account-detail")
    public String accountDetail(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "账户明细");
        model.addAttribute("activeMenu", "account-detail");
        return "account-detail";
    }

    @GetMapping("/statement")
    public String statement(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "对账单");
        model.addAttribute("activeMenu", "statement");
        return "statement";
    }

    @GetMapping("/channel")
    public String channel(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "通道管理");
        model.addAttribute("activeMenu", "channel");
        return "channel";
    }

    @GetMapping("/transaction")
    public String transaction(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "交易明细");
        model.addAttribute("activeMenu", "transaction");
        return "transaction";
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

    @GetMapping("/api/dashboard/overview")
    @ResponseBody
    public Result<Map<String, Object>> getOverview() {
        return dashboardService.getOverview();
    }

    @GetMapping("/api/dashboard/trend")
    @ResponseBody
    public Result<Map<String, Object>> getTrend() {
        return dashboardService.getMonthlyTrend();
    }
}
