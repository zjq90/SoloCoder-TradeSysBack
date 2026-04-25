package com.tradesys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.Merchant;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.MerchantMapper;
import com.tradesys.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService {

    private final MerchantMapper merchantMapper;

    @Override
    public Result<List<Agent>> getPage(PageQuery pageQuery, Agent agent) {
        Page<Agent> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Agent> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(agent.getAgentName())) {
            wrapper.like(Agent::getAgentName, agent.getAgentName());
        }
        if (StrUtil.isNotBlank(agent.getAgentNo())) {
            wrapper.like(Agent::getAgentNo, agent.getAgentNo());
        }
        if (agent.getLevel() != null) {
            wrapper.eq(Agent::getLevel, agent.getLevel());
        }
        if (agent.getStatus() != null) {
            wrapper.eq(Agent::getStatus, agent.getStatus());
        }
        if (agent.getParentId() != null && agent.getParentId() > 0) {
            wrapper.eq(Agent::getParentId, agent.getParentId());
        }
        wrapper.orderByDesc(Agent::getCreateTime);

        Page<Agent> resultPage = this.page(page, wrapper);

        // 补充父级名称
        for (Agent a : resultPage.getRecords()) {
            if (a.getParentId() != null && a.getParentId() > 0) {
                Agent parent = this.getById(a.getParentId());
                if (parent != null) {
                    a.setParentName(parent.getAgentName());
                }
            }
            // 更新直属商户数量
            int merchantCount = merchantMapper.countByAgentId(a.getId());
            a.setDirectMerchantCount(merchantCount);
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<List<Agent>> getTree() {
        List<Agent> allAgents = this.list(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getStatus, 1)
                .orderByAsc(Agent::getLevel)
                .orderByAsc(Agent::getId));

        // 构建树形结构
        Map<Long, Agent> agentMap = allAgents.stream()
                .collect(Collectors.toMap(Agent::getId, a -> a));

        List<Agent> roots = new ArrayList<>();

        for (Agent agent : allAgents) {
            // 更新直属商户数量
            int merchantCount = merchantMapper.countByAgentId(agent.getId());
            agent.setDirectMerchantCount(merchantCount);

            if (agent.getParentId() == null || agent.getParentId() == 0) {
                roots.add(agent);
            } else {
                Agent parent = agentMap.get(agent.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(agent);
                }
            }
        }

        return Result.success(roots);
    }

    @Override
    public Result<List<Agent>> getChildren(Long parentId) {
        List<Agent> children = baseMapper.selectByParentId(parentId);

        for (Agent agent : children) {
            int merchantCount = merchantMapper.countByAgentId(agent.getId());
            agent.setDirectMerchantCount(merchantCount);
        }

        return Result.success(children);
    }

    @Override
    public Result<Agent> getDetail(Long id) {
        Agent agent = this.getById(id);
        if (agent == null) {
            return Result.error("代理商不存在");
        }

        // 补充父级名称
        if (agent.getParentId() != null && agent.getParentId() > 0) {
            Agent parent = this.getById(agent.getParentId());
            if (parent != null) {
                agent.setParentName(parent.getAgentName());
            }
        }

        // 更新直属商户数量
        int merchantCount = merchantMapper.countByAgentId(agent.getId());
        agent.setDirectMerchantCount(merchantCount);

        return Result.success(agent);
    }

    @Override
    public Result<Map<String, Object>> getStats(Long agentId) {
        Map<String, Object> stats = new HashMap<>();

        Agent agent = this.getById(agentId);
        if (agent == null) {
            return Result.error("代理商不存在");
        }

        // 直属商户数量
        int directMerchantCount = merchantMapper.countByAgentId(agentId);
        stats.put("directMerchantCount", directMerchantCount);

        // 下级代理商数量
        long childAgentCount = this.count(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getParentId, agentId)
                .eq(Agent::getStatus, 1));
        stats.put("childAgentCount", childAgentCount);

        // 总商户数量（包括下级）
        int totalMerchantCount = baseMapper.countTotalMerchants(agentId);
        stats.put("totalMerchantCount", totalMerchantCount);

        // 机器数量
        long machineCount = 0;
        stats.put("machineCount", machineCount);

        return Result.success(stats);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addAgent(Agent agent) {
        // 生成代理商编号
        String agentNo = generateAgentNo();
        agent.setAgentNo(agentNo);

        // 确定级别
        if (agent.getParentId() == null || agent.getParentId() == 0) {
            agent.setLevel(1);
        } else {
            Agent parent = this.getById(agent.getParentId());
            if (parent == null) {
                return Result.error("上级代理商不存在");
            }
            agent.setLevel(parent.getLevel() + 1);
        }

        agent.setDirectMerchantCount(0);
        agent.setTotalMerchantCount(0);
        agent.setStatus(1);

        this.save(agent);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateAgent(Agent agent) {
        Agent existAgent = this.getById(agent.getId());
        if (existAgent == null) {
            return Result.error("代理商不存在");
        }

        // 不允许修改parentId和level，防止层级混乱
        agent.setParentId(null);
        agent.setLevel(null);

        this.updateById(agent);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteAgent(Long id) {
        Agent agent = this.getById(id);
        if (agent == null) {
            return Result.error("代理商不存在");
        }

        // 检查是否有下级代理商
        long childCount = this.count(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getParentId, id));
        if (childCount > 0) {
            return Result.error("存在下级代理商，无法删除");
        }

        // 检查是否有商户
        int merchantCount = merchantMapper.countByAgentId(id);
        if (merchantCount > 0) {
            return Result.error("存在关联商户，无法删除");
        }

        this.removeById(id);

        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getAgentOptions() {
        List<Agent> agents = this.list(new LambdaQueryWrapper<Agent>()
                .eq(Agent::getStatus, 1)
                .orderByAsc(Agent::getLevel)
                .orderByAsc(Agent::getId));

        List<Map<String, Object>> options = new ArrayList<>();
        for (Agent agent : agents) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", agent.getId());
            option.put("agentNo", agent.getAgentNo());
            option.put("agentName", agent.getAgentName());
            option.put("parentId", agent.getParentId());
            option.put("level", agent.getLevel());
            options.add(option);
        }

        return Result.success(options);
    }

    private String generateAgentNo() {
        String prefix = "AGT";
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        long count = this.count(new LambdaQueryWrapper<Agent>()
                .likeRight(Agent::getAgentNo, prefix + dateStr));

        return String.format("%s%s%06d", prefix, dateStr, count + 1);
    }
}
