package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.AgentAccount;
import com.tradesys.service.AgentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agent-account")
@RequiredArgsConstructor
public class AgentAccountController {

    private final AgentAccountService agentAccountService;

    @GetMapping("/api/list")
    public Result<List<AgentAccount>> list(PageQuery pageQuery, AgentAccount account) {
        return agentAccountService.getPage(pageQuery, account);
    }

    @GetMapping("/api/{id}")
    public Result<AgentAccount> getById(@PathVariable Long id) {
        return agentAccountService.getDetail(id);
    }

    @GetMapping("/api/by-agent/{agentId}")
    public Result<AgentAccount> getByAgentId(@PathVariable Long agentId) {
        return agentAccountService.getByAgentId(agentId);
    }

    @GetMapping("/api/options")
    public Result<List<Map<String, Object>>> options() {
        return agentAccountService.getOptions();
    }

    @PostMapping("/api/add-balance")
    public Result<Void> addBalance(@RequestParam Long agentId, 
                                    @RequestParam BigDecimal amount,
                                    @RequestParam(required = false) String remark) {
        return agentAccountService.addBalance(agentId, amount, remark);
    }

    @PostMapping("/api/update-status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        return agentAccountService.updateStatus(id, status);
    }

    @GetMapping("/api/export")
    public void export(AgentAccount account, HttpServletResponse response) {
        agentAccountService.exportExcel(account, response);
    }
}
