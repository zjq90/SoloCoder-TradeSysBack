package com.tradesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.AgentAccount;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AgentAccountService extends IService<AgentAccount> {

    Result<List<AgentAccount>> getPage(PageQuery pageQuery, AgentAccount account);

    Result<AgentAccount> getDetail(Long id);

    Result<AgentAccount> getByAgentId(Long agentId);

    Result<Void> addBalance(Long agentId, BigDecimal amount, String remark);

    Result<Void> freezeBalance(Long agentId, BigDecimal amount);

    Result<Void> unfreezeBalance(Long agentId, BigDecimal amount);

    Result<Void> updateStatus(Long id, Integer status);

    Result<List<Map<String, Object>>> getOptions();

    void exportExcel(AgentAccount account, HttpServletResponse response);
}
