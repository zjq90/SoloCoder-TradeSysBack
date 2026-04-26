package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Machine;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.service.MachineService;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/machine")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;
    private final SysUserService sysUserService;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "机器信息");
        model.addAttribute("activeMenu", "machine");
        return "machine";
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
    public Result<List<Machine>> list(PageQuery pageQuery, Machine machine) {
        return machineService.getPage(pageQuery, machine);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<Machine> getById(@PathVariable Long id) {
        return machineService.getDetail(id);
    }

    @GetMapping("/api/options")
    @ResponseBody
    public Result<List<Map<String, Object>>> options() {
        return machineService.getMachineOptions();
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Result<Void> add(@RequestBody Machine machine) {
        return machineService.addMachine(machine);
    }

    @PostMapping("/api/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Machine machine) {
        return machineService.updateMachine(machine);
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public Result<Void> delete(@PathVariable Long id) {
        return machineService.deleteMachine(id);
    }

    @PostMapping("/api/bind/{machineId}/{merchantId}")
    @ResponseBody
    public Result<Void> bind(@PathVariable Long machineId, @PathVariable Long merchantId) {
        return machineService.bindMerchant(machineId, merchantId);
    }

    @PostMapping("/api/unbind/{machineId}")
    @ResponseBody
    public Result<Void> unbind(@PathVariable Long machineId) {
        return machineService.unbindMerchant(machineId);
    }

    @GetMapping("/api/export/template")
    public void exportTemplate(HttpServletResponse response) {
        machineService.exportTemplate(response);
    }

    @PostMapping("/api/import")
    @ResponseBody
    public Result<Map<String, Object>> importMachines(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "agentId", required = false) Long agentId) {
        return machineService.importMachines(file, agentId);
    }
}
