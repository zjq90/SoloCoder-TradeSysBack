package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public String list() {
        return "user/agent";
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
