package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Agent;
import com.tradesys.entity.ProfitShare;
import com.tradesys.entity.Statement;
import com.tradesys.entity.Transaction;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.ProfitShareMapper;
import com.tradesys.mapper.StatementMapper;
import com.tradesys.mapper.TransactionMapper;
import com.tradesys.service.StatementService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementServiceImpl extends ServiceImpl<StatementMapper, Statement> implements StatementService {

    private final AgentMapper agentMapper;
    private final TransactionMapper transactionMapper;
    private final ProfitShareMapper profitShareMapper;

    @Override
    public Result<List<Statement>> getPage(PageQuery pageQuery, Statement statement,
                                            LocalDate startDate, LocalDate endDate) {
        Page<Statement> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Statement> wrapper = new LambdaQueryWrapper<>();
        if (statement.getAgentId() != null) {
            wrapper.eq(Statement::getAgentId, statement.getAgentId());
        }
        if (statement.getStatus() != null) {
            wrapper.eq(Statement::getStatus, statement.getStatus());
        }
        if (startDate != null) {
            wrapper.ge(Statement::getStatDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Statement::getStatDate, endDate);
        }
        wrapper.orderByDesc(Statement::getStatDate);

        Page<Statement> resultPage = this.page(page, wrapper);

        for (Statement s : resultPage.getRecords()) {
            if (s.getAgentId() != null) {
                Agent agent = agentMapper.selectById(s.getAgentId());
                if (agent != null) {
                    s.setAgentName(agent.getAgentName());
                    s.setAgentNo(agent.getAgentNo());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<Statement> getDetail(Long id) {
        Statement statement = this.getById(id);
        if (statement == null) {
            return Result.error("对账单不存在");
        }

        if (statement.getAgentId() != null) {
            Agent agent = agentMapper.selectById(statement.getAgentId());
            if (agent != null) {
                statement.setAgentName(agent.getAgentName());
                statement.setAgentNo(agent.getAgentNo());
            }
        }

        return Result.success(statement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<Statement>> generateByDateRange(Long agentId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Result.error("请指定开始日期和结束日期");
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始日期不能晚于结束日期");
        }

        List<Agent> agents = new ArrayList<>();
        if (agentId != null) {
            Agent agent = agentMapper.selectById(agentId);
            if (agent == null) {
                return Result.error("代理商不存在");
            }
            agents.add(agent);
        } else {
            agents = agentMapper.selectList(
                new LambdaQueryWrapper<Agent>().eq(Agent::getStatus, 1)
            );
        }

        List<Statement> result = new ArrayList<>();
        for (Agent agent : agents) {
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                Statement statement = generateSingleStatement(agent, currentDate);
                if (statement != null) {
                    result.add(statement);
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Statement> generateByDate(Long agentId, LocalDate statDate) {
        if (agentId == null || statDate == null) {
            return Result.error("参数不完整");
        }

        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            return Result.error("代理商不存在");
        }

        Statement statement = generateSingleStatement(agent, statDate);
        return Result.success(statement);
    }

    private Statement generateSingleStatement(Agent agent, LocalDate statDate) {
        Statement exist = baseMapper.selectByAgentAndDate(agent.getId(), statDate);
        if (exist != null) {
            return exist;
        }

        LocalDateTime startTime = statDate.atStartOfDay();
        LocalDateTime endTime = statDate.atTime(LocalTime.MAX);

        BigDecimal totalTransAmount = transactionMapper.sumAmountByAgentAndTimeRange(agent.getId(), startTime, endTime);
        if (totalTransAmount == null) {
            totalTransAmount = BigDecimal.ZERO;
        }

        BigDecimal totalProfitAmount = profitShareMapper.sumByAgentAndTimeRange(agent.getId(), startTime, endTime);
        if (totalProfitAmount == null) {
            totalProfitAmount = BigDecimal.ZERO;
        }

        long transCount = transactionMapper.countByMerchantAndTimeRange(agent.getId(), startTime, endTime);

        Statement statement = new Statement();
        statement.setStatementNo("STM" + IdUtil.getSnowflake(1, 1).nextIdStr());
        statement.setAgentId(agent.getId());
        statement.setStatDate(statDate);
        statement.setTotalTransAmount(totalTransAmount);
        statement.setTotalTransCount((int) transCount);
        statement.setTotalFeeAmount(totalTransAmount.multiply(new BigDecimal("0.005")));
        statement.setTotalProfitAmount(totalProfitAmount);
        statement.setStatus(0);
        statement.setAgentName(agent.getAgentName());
        statement.setAgentNo(agent.getAgentNo());

        this.save(statement);

        return statement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> confirmStatement(Long id) {
        Statement statement = this.getById(id);
        if (statement == null) {
            return Result.error("对账单不存在");
        }
        if (statement.getStatus() == 1) {
            return Result.error("该对账单已确认");
        }

        statement.setStatus(1);
        statement.setConfirmTime(LocalDateTime.now());
        this.updateById(statement);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> raiseObjection(Long id, String remark) {
        Statement statement = this.getById(id);
        if (statement == null) {
            return Result.error("对账单不存在");
        }

        statement.setStatus(2);
        if (StrUtil.isNotBlank(remark)) {
            statement.setRemark(remark);
        }
        this.updateById(statement);

        return Result.ok();
    }

    @Override
    public void exportExcel(Statement statement, LocalDate startDate, LocalDate endDate,
                            HttpServletResponse response) {
        LambdaQueryWrapper<Statement> wrapper = new LambdaQueryWrapper<>();
        if (statement.getAgentId() != null) {
            wrapper.eq(Statement::getAgentId, statement.getAgentId());
        }
        if (statement.getStatus() != null) {
            wrapper.eq(Statement::getStatus, statement.getStatus());
        }
        if (startDate != null) {
            wrapper.ge(Statement::getStatDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Statement::getStatDate, endDate);
        }
        wrapper.orderByDesc(Statement::getStatDate);

        List<Statement> list = this.list(wrapper);

        for (Statement s : list) {
            if (s.getAgentId() != null) {
                Agent agent = agentMapper.selectById(s.getAgentId());
                if (agent != null) {
                    s.setAgentName(agent.getAgentName());
                    s.setAgentNo(agent.getAgentNo());
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("对账单");

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

            String[] headers = {"对账单号", "代理商编号", "代理商名称", "对账日期", "交易总额", "交易笔数",
                                "手续费总额", "分润总额", "状态", "确认时间", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 18 * 256);
            }

            int rowNum = 1;
            for (Statement s : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(s.getStatementNo() != null ? s.getStatementNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(s.getAgentNo() != null ? s.getAgentNo() : "");
                cell1.setCellStyle(centerStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(s.getAgentName() != null ? s.getAgentName() : "");

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(s.getStatDate() != null ? s.getStatDate().toString() : "");
                cell3.setCellStyle(centerStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(s.getTotalTransAmount() != null ? s.getTotalTransAmount().doubleValue() : 0);
                cell4.setCellStyle(moneyStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(s.getTotalTransCount() != null ? s.getTotalTransCount() : 0);
                cell5.setCellStyle(centerStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(s.getTotalFeeAmount() != null ? s.getTotalFeeAmount().doubleValue() : 0);
                cell6.setCellStyle(moneyStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(s.getTotalProfitAmount() != null ? s.getTotalProfitAmount().doubleValue() : 0);
                cell7.setCellStyle(moneyStyle);

                Cell cell8 = row.createCell(8);
                String statusText = "";
                if (s.getStatus() != null) {
                    switch (s.getStatus()) {
                        case 0: statusText = "待确认"; break;
                        case 1: statusText = "已确认"; break;
                        case 2: statusText = "有异议"; break;
                        default: statusText = "未知";
                    }
                }
                cell8.setCellValue(statusText);
                cell8.setCellStyle(centerStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(s.getConfirmTime() != null ? s.getConfirmTime().toString().replace("T", " ") : "");
                cell9.setCellStyle(centerStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(s.getCreateTime() != null ? s.getCreateTime().toString().replace("T", " ") : "");
                cell10.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("对账单列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出对账单Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public void exportStatementDetail(Long id, HttpServletResponse response) {
        Statement statement = this.getById(id);
        if (statement == null) {
            throw new RuntimeException("对账单不存在");
        }

        Agent agent = agentMapper.selectById(statement.getAgentId());

        LocalDateTime startTime = statement.getStatDate().atStartOfDay();
        LocalDateTime endTime = statement.getStatDate().atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionMapper.selectList(
            new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getAgentId, statement.getAgentId())
                .ge(Transaction::getTransTime, startTime)
                .le(Transaction::getTransTime, endTime)
        );

        List<ProfitShare> profitShares = profitShareMapper.selectList(
            new LambdaQueryWrapper<ProfitShare>()
                .eq(ProfitShare::getAgentId, statement.getAgentId())
                .ge(ProfitShare::getCreateTime, startTime)
                .le(ProfitShare::getCreateTime, endTime)
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet transSheet = workbook.createSheet("交易明细");
            Sheet profitSheet = workbook.createSheet("分润明细");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] transHeaders = {"交易编号", "商户", "交易类型", "交易金额", "手续费", "费率", "状态", "交易时间"};
            Row transHeaderRow = transSheet.createRow(0);
            for (int i = 0; i < transHeaders.length; i++) {
                Cell cell = transHeaderRow.createCell(i);
                cell.setCellValue(transHeaders[i]);
                cell.setCellStyle(headerStyle);
                transSheet.setColumnWidth(i, 18 * 256);
            }

            int transRowNum = 1;
            for (Transaction t : transactions) {
                Row row = transSheet.createRow(transRowNum++);
                row.createCell(0).setCellValue(t.getTransNo() != null ? t.getTransNo() : "");
                row.createCell(1).setCellValue(t.getMerchantId() != null ? t.getMerchantId().toString() : "");
                String transTypeText = "";
                if (t.getTransType() != null) {
                    switch (t.getTransType()) {
                        case 1: transTypeText = "消费"; break;
                        case 2: transTypeText = "撤销"; break;
                        case 3: transTypeText = "退款"; break;
                    }
                }
                row.createCell(2).setCellValue(transTypeText);
                row.createCell(3).setCellValue(t.getTransAmount() != null ? t.getTransAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(t.getFeeAmount() != null ? t.getFeeAmount().doubleValue() : 0);
                row.createCell(5).setCellValue(t.getRate() != null ? t.getRate().toString() : "");
                String statusText = "";
                if (t.getStatus() != null) {
                    switch (t.getStatus()) {
                        case 0: statusText = "处理中"; break;
                        case 1: statusText = "成功"; break;
                        case 2: statusText = "失败"; break;
                    }
                }
                row.createCell(6).setCellValue(statusText);
                row.createCell(7).setCellValue(t.getTransTime() != null ? t.getTransTime().toString().replace("T", " ") : "");
            }

            String[] profitHeaders = {"分润编号", "交易编号", "分润费率", "分润金额", "层级", "状态", "创建时间"};
            Row profitHeaderRow = profitSheet.createRow(0);
            for (int i = 0; i < profitHeaders.length; i++) {
                Cell cell = profitHeaderRow.createCell(i);
                cell.setCellValue(profitHeaders[i]);
                cell.setCellStyle(headerStyle);
                profitSheet.setColumnWidth(i, 18 * 256);
            }

            int profitRowNum = 1;
            for (ProfitShare p : profitShares) {
                Row row = profitSheet.createRow(profitRowNum++);
                row.createCell(0).setCellValue(p.getProfitNo() != null ? p.getProfitNo() : "");
                row.createCell(1).setCellValue(p.getTransNo() != null ? p.getTransNo() : "");
                row.createCell(2).setCellValue(p.getProfitRate() != null ? p.getProfitRate().toString() : "");
                row.createCell(3).setCellValue(p.getProfitAmount() != null ? p.getProfitAmount().doubleValue() : 0);
                row.createCell(4).setCellValue("第" + (p.getLevel() != null ? p.getLevel() : 1) + "级");
                row.createCell(5).setCellValue(p.getStatus() != null && p.getStatus() == 1 ? "已结算" : "待结算");
                row.createCell(6).setCellValue(p.getCreateTime() != null ? p.getCreateTime().toString().replace("T", " ") : "");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("对账单详情-" + (agent != null ? agent.getAgentName() : "") + "-" + statement.getStatDate(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出对账单详情Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
