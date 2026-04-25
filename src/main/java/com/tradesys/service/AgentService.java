package com.tradesys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;

import java.util.List;
import java.util.Map;

public interface AgentService extends IService<Agent> {

    Result<List<Agent>> getPage(PageQuery pageQuery, Agent agent);

    Result<List<Agent>> getTree();

    Result<List<Agent>> getChildren(Long parentId);

    Result<Agent> getDetail(Long id);

    Result<Map<String, Object>> getStats(Long agentId);

    Result<Void> addAgent(Agent agent);

    Result<Void> updateAgent(Agent agent);

    Result<Void> deleteAgent(Long id);

    Result<List<Map<String, Object>>> getAgentOptions();
}
