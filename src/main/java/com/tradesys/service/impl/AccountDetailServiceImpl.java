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
import com.tradesys.mapper.AccountDetailMapper;
import com.tradesys.mapper.AgentMapper;
import com.tradesys.service.AccountDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailMapper, AccountDetail> implements AccountDetailService {

    private final AgentMapper agentMapper;

    @Override
    public Result<List<AccountDetail>> getPage(PageQuery pageQuery, AccountDetail detail,
                                               LocalDate startDate, LocalDate endDate) {
        Page<AccountDetail> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<AccountDetail> wrapper = new LambdaQueryWrapper<>();
        if (detail.getAgentId() != null) {
            wrapper.eq(AccountDetail::getAgentId, detail.getAgentId());
        }
        if (detail.getAccountType() != null) {
            wrapper.eq(AccountDetail::getAccountType, detail.getAccountType());
        }
        if (detail.getTransType() != null) {
            wrapper.eq(AccountDetail::getTransType, detail.getTransType());
        }
        if (StrUtil.isNotBlank(detail.getRelateNo())) {
            wrapper.like(AccountDetail::getRelateNo, detail.getRelateNo());
        }
        if (startDate != null) {
            wrapper.ge(AccountDetail::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(AccountDetail::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(AccountDetail::getCreateTime);

        Page<AccountDetail> resultPage = this.page(page, wrapper);

        for (AccountDetail ad : resultPage.getRecords()) {
            if (ad.getAgentId() != null) {
                Agent agent = agentMapper.selectById(ad.getAgentId());
                if (agent != null) {
                    ad.setAgentName(agent.getAgentName());
                    ad.setAgentNo(agent.getAgentNo());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<AccountDetail> getDetail(Long id) {
        AccountDetail detail = this.getById(id);
        if (detail == null) {
            return Result.error("账户明细不存在");
        }

        if (detail.getAgentId() != null) {
            Agent agent = agentMapper.selectById(detail.getAgentId());
            if (agent != null) {
                detail.setAgentName(agent.getAgentName());
                detail.setAgentNo(agent.getAgentNo());
            }
        }

        return Result.success(detail);
    }

    @Override
    public Result<List<Map<String, Object>>> getStatsByAgent(Long agentId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<AccountDetail> wrapper = new LambdaQueryWrapper<>();
        if (agentId != null) {
            wrapper.eq(AccountDetail::getAgentId, agentId);
        }
        if (startDate != null) {
            wrapper.ge(AccountDetail::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(AccountDetail::getCreateTime, endDate.atTime(LocalTime.MAX));
        }

        List<AccountDetail> list = this.list(wrapper);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (AccountDetail ad : list) {
            if (ad.getAmount() != null) {
                if (ad.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    totalIncome = totalIncome.add(ad.getAmount());
                } else {
                    totalExpense = totalExpense.add(ad.getAmount().abs());
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("totalIncome", totalIncome);
        map.put("totalExpense", totalExpense);
        map.put("balance", totalIncome.subtract(totalExpense));
        result.add(map);

        return Result.success(result);
    }

    @Override
    public void exportExcel(AccountDetail detail, LocalDate startDate, LocalDate endDate,
                            HttpServletResponse response) {
        LambdaQueryWrapper<AccountDetail> wrapper = new LambdaQueryWrapper<>();
        if (detail.getAgentId() != null) {
            wrapper.eq(AccountDetail::getAgentId, detail.getAgentId());
        }
        if (detail.getAccountType() != null) {
            wrapper.eq(AccountDetail::getAccountType, detail.getAccountType());
        }
        if (detail.getTransType() != null) {
            wrapper.eq(AccountDetail::getTransType, detail.getTransType());
        }
        if (StrUtil.isNotBlank(detail.getRelateNo())) {
            wrapper.like(AccountDetail::getRelateNo, detail.getRelateNo());
        }
        if (startDate != null) {
            wrapper.ge(AccountDetail::getCreateTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(AccountDetail::getCreateTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(AccountDetail::getCreateTime);

        List<AccountDetail> list = this.list(wrapper);

        for (AccountDetail ad : list) {
            if (ad.getAgentId() != null) {
                Agent agent = agentMapper.selectById(ad.getAgentId());
                if (agent != null) {
                    ad.setAgentName(agent.getAgentName());
                    ad.setAgentNo(agent.getAgentNo());
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("账户明细");

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

            String[] headers = {"明细单号", "代理商编号", "代理商名称", "账户类型", "交易类型", 
                                "变动金额", "变动前余额", "变动后余额", "关联单号", "备注", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (AccountDetail ad : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(ad.getDetailNo() != null ? ad.getDetailNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(ad.getAgentNo() != null ? ad.getAgentNo() : "");
                cell1.setCellStyle(centerStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(ad.getAgentName() != null ? ad.getAgentName() : "");

                Cell cell3 = row.createCell(3);
                String accountTypeText = "";
                if (ad.getAccountType() != null) {
                    switch (ad.getAccountType()) {
                        case 1: accountTypeText = "分润账户"; break;
                        case 2: accountTypeText = "提现账户"; break;
                        default: accountTypeText = "未知";
                    }
                }
                cell3.setCellValue(accountTypeText);
                cell3.setCellStyle(centerStyle);

                Cell cell4 = row.createCell(4);
                String transTypeText = "";
                if (ad.getTransType() != null) {
                    switch (ad.getTransType()) {
                        case 1: transTypeText = "分润收入"; break;
                        case 2: transTypeText = "提现"; break;
                        case 3: transTypeText = "提现退款"; break;
                        case 4: transTypeText = "调账"; break;
                        default: transTypeText = "未知";
                    }
                }
                cell4.setCellValue(transTypeText);
                cell4.setCellStyle(centerStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(ad.getAmount() != null ? ad.getAmount().doubleValue() : 0);
                cell5.setCellStyle(moneyStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(ad.getBeforeBalance() != null ? ad.getBeforeBalance().doubleValue() : 0);
                cell6.setCellStyle(moneyStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(ad.getAfterBalance() != null ? ad.getAfterBalance().doubleValue() : 0);
                cell7.setCellStyle(moneyStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(ad.getRelateNo() != null ? ad.getRelateNo() : "");

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(ad.getRemark() != null ? ad.getRemark() : "");

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(ad.getCreateTime() != null ? ad.getCreateTime().toString().replace("T", " ") : "");
                cell10.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("账户明细列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出账户明细Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
