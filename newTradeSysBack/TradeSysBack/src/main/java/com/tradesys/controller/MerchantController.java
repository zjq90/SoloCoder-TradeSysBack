package com.tradesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.Merchant;
import com.tradesys.entity.SysRole;
import com.tradesys.entity.SysUser;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.MerchantMapper;
import com.tradesys.service.SysUserService;
import com.tradesys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantMapper merchantMapper;
    private final AgentMapper agentMapper;
    private final SysUserService sysUserService;

    @GetMapping
    public String list(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("pageTitle", "商户管理");
        model.addAttribute("activeMenu", "merchant");
        return "merchant";
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
    public Result<List<Merchant>> list(PageQuery pageQuery, Merchant merchant) {
        Page<Merchant> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (merchant.getMerchantName() != null && !merchant.getMerchantName().isEmpty()) {
            wrapper.like(Merchant::getMerchantName, merchant.getMerchantName());
        }
        if (merchant.getParentId() != null && merchant.getParentId() > 0) {
            wrapper.eq(Merchant::getParentId, merchant.getParentId());
        }
        if (merchant.getStatus() != null) {
            wrapper.eq(Merchant::getStatus, merchant.getStatus());
        }
        wrapper.orderByDesc(Merchant::getCreateTime);

        Page<Merchant> resultPage = merchantMapper.selectPage(page, wrapper);

        // 补充代理商信息
        for (Merchant m : resultPage.getRecords()) {
            if (m.getParentId() != null) {
                Agent agent = agentMapper.selectById(m.getParentId());
                if (agent != null) {
                    m.setParentName(agent.getAgentName());
                    m.setAgentName(agent.getAgentName());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return Result.error("商户不存在");
        }

        if (merchant.getParentId() != null) {
            Agent agent = agentMapper.selectById(merchant.getParentId());
            if (agent != null) {
                merchant.setParentName(agent.getAgentName());
                merchant.setAgentName(agent.getAgentName());
            }
        }

        return Result.success(merchant);
    }

    @GetMapping("/api/by-agent/{agentId}")
    @ResponseBody
    public Result<List<Merchant>> getByAgentId(@PathVariable Long agentId) {
        List<Merchant> merchants = merchantMapper.selectByAgentId(agentId);
        return Result.success(merchants);
    }

    @PostMapping("/api/add")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> add(@RequestBody Merchant merchant) {
        // 生成商户编号
        merchant.setMerchantNo(generateMerchantNo());

        // 构建层级路径
        if (merchant.getParentId() != null && merchant.getParentId() > 0) {
            Agent agent = agentMapper.selectById(merchant.getParentId());
            if (agent == null) {
                return Result.error("所属代理商不存在");
            }
            merchant.setParentPath("/" + agent.getId() + "/");

            // 更新代理商直属商户数量
            agentMapper.incrementDirectMerchantCount(merchant.getParentId());
        }

        merchant.setStatus(1);
        merchantMapper.insert(merchant);

        return Result.ok();
    }

    @PostMapping("/api/update")
    @ResponseBody
    public Result<Void> update(@RequestBody Merchant merchant) {
        Merchant exist = merchantMapper.selectById(merchant.getId());
        if (exist == null) {
            return Result.error("商户不存在");
        }

        // 不允许修改所属代理商
        merchant.setParentId(null);
        merchant.setParentPath(null);

        merchantMapper.updateById(merchant);

        return Result.ok();
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return Result.error("商户不存在");
        }

        // TODO: 检查是否有绑定的机器或交易记录

        if (merchant.getParentId() != null) {
            agentMapper.decrementDirectMerchantCount(merchant.getParentId());
        }

        merchantMapper.deleteById(id);

        return Result.ok();
    }

    private String generateMerchantNo() {
        String prefix = "MCH";
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        Long count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>()
                .likeRight(Merchant::getMerchantNo, prefix + dateStr));

        return String.format("%s%s%06d", prefix, dateStr, count + 1);
    }
}
