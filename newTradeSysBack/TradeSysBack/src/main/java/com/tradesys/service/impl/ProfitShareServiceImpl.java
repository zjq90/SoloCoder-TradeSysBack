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
import com.tradesys.entity.Product;
import com.tradesys.entity.ProfitShare;
import com.tradesys.mapper.AccountDetailMapper;
import com.tradesys.mapper.AgentAccountMapper;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.mapper.ProductMapper;
import com.tradesys.mapper.ProfitShareMapper;
import com.tradesys.service.ProfitShareService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfitShareServiceImpl extends ServiceImpl<ProfitShareMapper, ProfitShare> implements ProfitShareService {

    private final AgentMapper agentMapper;
    private final ProductMapper productMapper;
    private final AgentAccountMapper agentAccountMapper;
    private final AccountDetailMapper accountDetailMapper;

    @Override
    public Result<List<ProfitShare>> getPage(PageQuery pageQuery, ProfitShare profit, 
                                               LocalDate startDate, LocalDate endDate) {
        Page<ProfitShare> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<ProfitShare> wrapper = new LambdaQueryWrapper<>();
        if (profit.getAgentId() != null) {
            wrapper.eq(ProfitShare::getAgentId, profit.getAgentId());
        }
        if (StrUtil.isNotBlank(profit.getTransNo())) {
            wrapper.eq(ProfitShare::getTransNo, profit.getTransNo());
        }
        if (profit.getStatus() != null) {
            wrapper.eq(ProfitShare::getStatus, profit.getStatus());
        }
        if (profit.getProductId() != null) {
            wrapper.eq(ProfitShare::getProductId, profit.getProductId());
        }
        if (startDate != null) {
            wrapper.ge(ProfitShare::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(ProfitShare::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(ProfitShare::getCreateTime);

        Page<ProfitShare> resultPage = this.page(page, wrapper);

        for (ProfitShare ps : resultPage.getRecords()) {
            if (ps.getAgentId() != null) {
                Agent agent = agentMapper.selectById(ps.getAgentId());
                if (agent != null) {
                    ps.setAgentName(agent.getAgentName());
                }
            }
            if (ps.getParentAgentId() != null) {
                Agent parent = agentMapper.selectById(ps.getParentAgentId());
                if (parent != null) {
                    ps.setParentAgentName(parent.getAgentName());
                }
            }
            if (ps.getProductId() != null) {
                Product product = productMapper.selectById(ps.getProductId());
                if (product != null) {
                    ps.setProductName(product.getProductName());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<ProfitShare> getDetail(Long id) {
        ProfitShare profit = this.getById(id);
        if (profit == null) {
            return Result.error("分润记录不存在");
        }

        if (profit.getAgentId() != null) {
            Agent agent = agentMapper.selectById(profit.getAgentId());
            if (agent != null) {
                profit.setAgentName(agent.getAgentName());
            }
        }
        if (profit.getParentAgentId() != null) {
            Agent parent = agentMapper.selectById(profit.getParentAgentId());
            if (parent != null) {
                profit.setParentAgentName(parent.getAgentName());
            }
        }
        if (profit.getProductId() != null) {
            Product product = productMapper.selectById(profit.getProductId());
            if (product != null) {
                profit.setProductName(product.getProductName());
            }
        }

        return Result.success(profit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> settleProfit(Long id) {
        ProfitShare profit = this.getById(id);
        if (profit == null) {
            return Result.error("分润记录不存在");
        }
        if (profit.getStatus() == 1) {
            return Result.error("该分润已结算");
        }

        profit.setStatus(1);
        profit.setSettleTime(LocalDateTime.now());
        this.updateById(profit);

        agentAccountMapper.addBalance(profit.getAgentId(), profit.getProfitAmount());

        AgentAccount account = agentAccountMapper.selectById(profit.getAgentId());
        AccountDetail detail = new AccountDetail();
        detail.setDetailNo("AD" + IdUtil.getSnowflake(1, 1).nextIdStr());
        detail.setAgentId(profit.getAgentId());
        detail.setAccountType(1);
        detail.setTransType(1);
        detail.setAmount(profit.getProfitAmount());
        detail.setBeforeBalance(account != null ? account.getBalance().subtract(profit.getProfitAmount()) : BigDecimal.ZERO);
        detail.setAfterBalance(account != null ? account.getBalance() : profit.getProfitAmount());
        detail.setRelateNo(profit.getProfitNo());
        detail.setRemark("分润结算");
        accountDetailMapper.insert(detail);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchSettle(List<Long> ids) {
        for (Long id : ids) {
            settleProfit(id);
        }
        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getStatsByAgent(Long agentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        if (agentId != null) {
            BigDecimal total = baseMapper.sumByAgentAndTimeRange(agentId, startTime, endTime);
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("agentId", agentId);
            map.put("totalProfit", total);
            result.add(map);
            return Result.success(result);
        } else {
            BigDecimal total = baseMapper.sumByTimeRange(startTime, endTime);
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("totalProfit", total);
            result.add(map);
            return Result.success(result);
        }
    }

    @Override
    public void exportExcel(ProfitShare profit, LocalDate startDate, LocalDate endDate,
                            HttpServletResponse response) {
        LambdaQueryWrapper<ProfitShare> wrapper = new LambdaQueryWrapper<>();
        if (profit.getAgentId() != null) {
            wrapper.eq(ProfitShare::getAgentId, profit.getAgentId());
        }
        if (StrUtil.isNotBlank(profit.getTransNo())) {
            wrapper.eq(ProfitShare::getTransNo, profit.getTransNo());
        }
        if (profit.getStatus() != null) {
            wrapper.eq(ProfitShare::getStatus, profit.getStatus());
        }
        if (profit.getProductId() != null) {
            wrapper.eq(ProfitShare::getProductId, profit.getProductId());
        }
        if (startDate != null) {
            wrapper.ge(ProfitShare::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(ProfitShare::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(ProfitShare::getCreateTime);

        List<ProfitShare> list = this.list(wrapper);

        for (ProfitShare ps : list) {
            if (ps.getAgentId() != null) {
                Agent agent = agentMapper.selectById(ps.getAgentId());
                if (agent != null) {
                    ps.setAgentName(agent.getAgentName());
                }
            }
            if (ps.getParentAgentId() != null) {
                Agent parent = agentMapper.selectById(ps.getParentAgentId());
                if (parent != null) {
                    ps.setParentAgentName(parent.getAgentName());
                }
            }
            if (ps.getProductId() != null) {
                Product product = productMapper.selectById(ps.getProductId());
                if (product != null) {
                    ps.setProductName(product.getProductName());
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("分润明细");

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

            String[] headers = {"分润编号", "交易编号", "代理商", "上级代理", "产品", "交易金额", 
                                "分润费率", "分润金额", "层级", "状态", "结算时间", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (ProfitShare ps : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(ps.getProfitNo() != null ? ps.getProfitNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(ps.getTransNo() != null ? ps.getTransNo() : "");

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(ps.getAgentName() != null ? ps.getAgentName() : "");

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(ps.getParentAgentName() != null ? ps.getParentAgentName() : "");

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(ps.getProductName() != null ? ps.getProductName() : "");

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(ps.getTransAmount() != null ? ps.getTransAmount().doubleValue() : 0);
                cell5.setCellStyle(moneyStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(ps.getProfitRate() != null ? ps.getProfitRate().toString() : "");
                cell6.setCellStyle(centerStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(ps.getProfitAmount() != null ? ps.getProfitAmount().doubleValue() : 0);
                cell7.setCellStyle(moneyStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue("第" + (ps.getLevel() != null ? ps.getLevel() : 1) + "级");
                cell8.setCellStyle(centerStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(ps.getStatus() != null && ps.getStatus() == 1 ? "已结算" : "待结算");
                cell9.setCellStyle(centerStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(ps.getSettleTime() != null ? ps.getSettleTime().toString().replace("T", " ") : "");
                cell10.setCellStyle(centerStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(ps.getCreateTime() != null ? ps.getCreateTime().toString().replace("T", " ") : "");
                cell11.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("分润明细列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出分润明细Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
