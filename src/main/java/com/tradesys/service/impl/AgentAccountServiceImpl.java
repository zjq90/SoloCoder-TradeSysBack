package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.AccountDetail;
import com.tradesys.entity.Agent;
import com.tradesys.entity.AgentAccount;
import com.tradesys.mapper.AccountDetailMapper;
import com.tradesys.mapper.AgentAccountMapper;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.service.AgentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentAccountServiceImpl extends ServiceImpl<AgentAccountMapper, AgentAccount> implements AgentAccountService {

    private final AgentMapper agentMapper;
    private final AccountDetailMapper accountDetailMapper;

    @Override
    public Result<List<AgentAccount>> getPage(PageQuery pageQuery, AgentAccount account) {
        Page<AgentAccount> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<AgentAccount> wrapper = new LambdaQueryWrapper<>();
        if (account.getAgentId() != null) {
            wrapper.eq(AgentAccount::getAgentId, account.getAgentId());
        }
        if (account.getStatus() != null) {
            wrapper.eq(AgentAccount::getStatus, account.getStatus());
        }
        wrapper.orderByDesc(AgentAccount::getCreateTime);

        Page<AgentAccount> resultPage = this.page(page, wrapper);

        for (AgentAccount aa : resultPage.getRecords()) {
            if (aa.getAgentId() != null) {
                Agent agent = agentMapper.selectById(aa.getAgentId());
                if (agent != null) {
                    aa.setAgentName(agent.getAgentName());
                    aa.setAgentNo(agent.getAgentNo());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<AgentAccount> getDetail(Long id) {
        AgentAccount account = this.getById(id);
        if (account == null) {
            return Result.error("账户不存在");
        }

        if (account.getAgentId() != null) {
            Agent agent = agentMapper.selectById(account.getAgentId());
            if (agent != null) {
                account.setAgentName(agent.getAgentName());
                account.setAgentNo(agent.getAgentNo());
            }
        }

        return Result.success(account);
    }

    @Override
    public Result<AgentAccount> getByAgentId(Long agentId) {
        AgentAccount account = baseMapper.selectDetailByAgentId(agentId);
        if (account == null) {
            Agent agent = agentMapper.selectById(agentId);
            if (agent != null) {
                account = new AgentAccount();
                account.setAgentId(agentId);
                account.setBalance(BigDecimal.ZERO);
                account.setFrozenAmount(BigDecimal.ZERO);
                account.setTotalIncome(BigDecimal.ZERO);
                account.setTotalWithdraw(BigDecimal.ZERO);
                account.setStatus(1);
                this.save(account);
                
                account.setAgentName(agent.getAgentName());
                account.setAgentNo(agent.getAgentNo());
            }
        }
        return Result.success(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addBalance(Long agentId, BigDecimal amount, String remark) {
        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            return Result.error("代理商不存在");
        }

        AgentAccount account = baseMapper.selectDetailByAgentId(agentId);
        if (account == null) {
            account = new AgentAccount();
            account.setAgentId(agentId);
            account.setBalance(amount);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setTotalIncome(amount);
            account.setTotalWithdraw(BigDecimal.ZERO);
            account.setStatus(1);
            this.save(account);
        } else {
            baseMapper.addBalance(agentId, amount);
        }

        AccountDetail detail = new AccountDetail();
        detail.setDetailNo("AD" + IdUtil.getSnowflake(1, 1).nextIdStr());
        detail.setAgentId(agentId);
        detail.setAccountType(1);
        detail.setTransType(4);
        detail.setAmount(amount);
        detail.setBeforeBalance(account != null ? account.getBalance() : BigDecimal.ZERO);
        detail.setAfterBalance((account != null ? account.getBalance() : BigDecimal.ZERO).add(amount));
        detail.setRemark(StrUtil.isNotBlank(remark) ? remark : "人工调账");
        accountDetailMapper.insert(detail);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> freezeBalance(Long agentId, BigDecimal amount) {
        int result = baseMapper.freezeBalance(agentId, amount);
        if (result <= 0) {
            return Result.error("余额不足");
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unfreezeBalance(Long agentId, BigDecimal amount) {
        int result = baseMapper.unfreezeBalance(agentId, amount);
        if (result <= 0) {
            return Result.error("冻结余额不足");
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateStatus(Long id, Integer status) {
        AgentAccount account = this.getById(id);
        if (account == null) {
            return Result.error("账户不存在");
        }
        account.setStatus(status);
        this.updateById(account);
        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getOptions() {
        List<AgentAccount> accounts = this.list(
            new LambdaQueryWrapper<AgentAccount>()
                .eq(AgentAccount::getStatus, 1)
                .orderByDesc(AgentAccount::getCreateTime)
        );

        List<Map<String, Object>> options = new ArrayList<>();
        for (AgentAccount account : accounts) {
            if (account.getAgentId() != null) {
                Agent agent = agentMapper.selectById(account.getAgentId());
                if (agent != null) {
                    Map<String, Object> option = new HashMap<>();
                    option.put("id", account.getId());
                    option.put("agentId", account.getAgentId());
                    option.put("agentName", agent.getAgentName());
                    option.put("agentNo", agent.getAgentNo());
                    option.put("balance", account.getBalance());
                    options.add(option);
                }
            }
        }

        return Result.success(options);
    }

    @Override
    public void exportExcel(AgentAccount account, HttpServletResponse response) {
        LambdaQueryWrapper<AgentAccount> wrapper = new LambdaQueryWrapper<>();
        if (account.getAgentId() != null) {
            wrapper.eq(AgentAccount::getAgentId, account.getAgentId());
        }
        if (account.getStatus() != null) {
            wrapper.eq(AgentAccount::getStatus, account.getStatus());
        }
        wrapper.orderByDesc(AgentAccount::getCreateTime);

        List<AgentAccount> list = this.list(wrapper);

        for (AgentAccount aa : list) {
            if (aa.getAgentId() != null) {
                Agent agent = agentMapper.selectById(aa.getAgentId());
                if (agent != null) {
                    aa.setAgentName(agent.getAgentName());
                    aa.setAgentNo(agent.getAgentNo());
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("代理账户");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat moneyFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(moneyFormat.getFormat("#,##0.00"));

            String[] headers = {"代理商编号", "代理商名称", "账户余额", "冻结金额", "累计收益", "累计提现", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (AgentAccount aa : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(aa.getAgentNo() != null ? aa.getAgentNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(aa.getAgentName() != null ? aa.getAgentName() : "");

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(aa.getBalance() != null ? aa.getBalance().doubleValue() : 0);
                cell2.setCellStyle(moneyStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(aa.getFrozenAmount() != null ? aa.getFrozenAmount().doubleValue() : 0);
                cell3.setCellStyle(moneyStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(aa.getTotalIncome() != null ? aa.getTotalIncome().doubleValue() : 0);
                cell4.setCellStyle(moneyStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(aa.getTotalWithdraw() != null ? aa.getTotalWithdraw().doubleValue() : 0);
                cell5.setCellStyle(moneyStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(aa.getStatus() != null && aa.getStatus() == 1 ? "正常" : "冻结");
                cell6.setCellStyle(centerStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(aa.getCreateTime() != null ? aa.getCreateTime().toString().replace("T", " ") : "");
                cell7.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("代理账户列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出代理账户Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
