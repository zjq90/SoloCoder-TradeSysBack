package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Channel;
import com.tradesys.entity.Machine;
import com.tradesys.entity.Merchant;
import com.tradesys.entity.Product;
import com.tradesys.entity.Transaction;
import com.tradesys.mapper.ChannelMapper;
import com.tradesys.mapper.MachineMapper;
import com.tradesys.mapper.MerchantMapper;
import com.tradesys.mapper.ProductMapper;
import com.tradesys.mapper.TransactionMapper;
import com.tradesys.service.TransactionService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final MachineMapper machineMapper;
    private final ChannelMapper channelMapper;

    @Override
    public Result<List<Transaction>> getPage(PageQuery pageQuery, Transaction transaction,
                                               LocalDate startDate, LocalDate endDate) {
        Page<Transaction> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(transaction.getTransNo())) {
            wrapper.like(Transaction::getTransNo, transaction.getTransNo());
        }
        if (StrUtil.isNotBlank(transaction.getOutTransNo())) {
            wrapper.like(Transaction::getOutTransNo, transaction.getOutTransNo());
        }
        if (transaction.getMerchantId() != null) {
            wrapper.eq(Transaction::getMerchantId, transaction.getMerchantId());
        }
        if (transaction.getAgentId() != null) {
            wrapper.eq(Transaction::getAgentId, transaction.getAgentId());
        }
        if (transaction.getProductId() != null) {
            wrapper.eq(Transaction::getProductId, transaction.getProductId());
        }
        if (transaction.getChannelId() != null) {
            wrapper.eq(Transaction::getChannelId, transaction.getChannelId());
        }
        if (transaction.getTransType() != null) {
            wrapper.eq(Transaction::getTransType, transaction.getTransType());
        }
        if (transaction.getStatus() != null) {
            wrapper.eq(Transaction::getStatus, transaction.getStatus());
        }
        if (startDate != null) {
            wrapper.ge(Transaction::getTransTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(Transaction::getTransTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(Transaction::getTransTime);

        Page<Transaction> resultPage = this.page(page, wrapper);

        for (Transaction t : resultPage.getRecords()) {
            if (t.getMerchantId() != null) {
                Merchant merchant = merchantMapper.selectById(t.getMerchantId());
                if (merchant != null) {
                    t.setMerchantName(merchant.getMerchantName());
                }
            }
            if (t.getAgentId() != null) {
                com.tradesys.entity.Agent agent = new com.tradesys.entity.Agent();
                agent.setId(t.getAgentId());
            }
            if (t.getProductId() != null) {
                Product product = productMapper.selectById(t.getProductId());
                if (product != null) {
                    t.setProductName(product.getProductName());
                }
            }
            if (t.getMachineId() != null) {
                Machine machine = machineMapper.selectById(t.getMachineId());
                if (machine != null) {
                    t.setMachineNo(machine.getMachineNo());
                }
            }
            if (t.getChannelId() != null) {
                Channel channel = channelMapper.selectById(t.getChannelId());
                if (channel != null) {
                    t.setChannelName(channel.getChannelName());
                }
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<Transaction> getDetail(Long id) {
        Transaction transaction = baseMapper.selectDetailById(id);
        if (transaction == null) {
            return Result.error("交易记录不存在");
        }
        return Result.success(transaction);
    }

    @Override
    public Result<List<Map<String, Object>>> getDailyStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Map<String, Object>> stats = baseMapper.selectDailyStatistics(startTime, endTime);
        return Result.success(stats);
    }

    @Override
    public Result<Map<String, Object>> getOverviewStats() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now();
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.atTime(LocalTime.MAX);

        Map<String, Object> result = new HashMap<>();
        result.put("todayCount", baseMapper.countByTimeRange(todayStart, todayEnd));
        result.put("todayAmount", baseMapper.sumAmountByTimeRange(todayStart, todayEnd));
        result.put("yesterdayCount", baseMapper.countByTimeRange(yesterdayStart, yesterdayEnd));
        result.put("yesterdayAmount", baseMapper.sumAmountByTimeRange(yesterdayStart, yesterdayEnd));

        return Result.success(result);
    }

    @Override
    public void exportExcel(Transaction transaction, LocalDate startDate, LocalDate endDate,
                            HttpServletResponse response) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(transaction.getTransNo())) {
            wrapper.like(Transaction::getTransNo, transaction.getTransNo());
        }
        if (StrUtil.isNotBlank(transaction.getOutTransNo())) {
            wrapper.like(Transaction::getOutTransNo, transaction.getOutTransNo());
        }
        if (transaction.getMerchantId() != null) {
            wrapper.eq(Transaction::getMerchantId, transaction.getMerchantId());
        }
        if (transaction.getAgentId() != null) {
            wrapper.eq(Transaction::getAgentId, transaction.getAgentId());
        }
        if (transaction.getProductId() != null) {
            wrapper.eq(Transaction::getProductId, transaction.getProductId());
        }
        if (transaction.getChannelId() != null) {
            wrapper.eq(Transaction::getChannelId, transaction.getChannelId());
        }
        if (transaction.getTransType() != null) {
            wrapper.eq(Transaction::getTransType, transaction.getTransType());
        }
        if (transaction.getStatus() != null) {
            wrapper.eq(Transaction::getStatus, transaction.getStatus());
        }
        if (startDate != null) {
            wrapper.ge(Transaction::getTransTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(Transaction::getTransTime, endDate.atTime(LocalTime.MAX));
        }
        wrapper.orderByDesc(Transaction::getTransTime);

        List<Transaction> list = this.list(wrapper);

        for (Transaction t : list) {
            if (t.getMerchantId() != null) {
                Merchant merchant = merchantMapper.selectById(t.getMerchantId());
                if (merchant != null) {
                    t.setMerchantName(merchant.getMerchantName());
                }
            }
            if (t.getProductId() != null) {
                Product product = productMapper.selectById(t.getProductId());
                if (product != null) {
                    t.setProductName(product.getProductName());
                }
            }
            if (t.getMachineId() != null) {
                Machine machine = machineMapper.selectById(t.getMachineId());
                if (machine != null) {
                    t.setMachineNo(machine.getMachineNo());
                }
            }
            if (t.getChannelId() != null) {
                Channel channel = channelMapper.selectById(t.getChannelId());
                if (channel != null) {
                    t.setChannelName(channel.getChannelName());
                }
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("交易明细");

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

            String[] headers = {"交易编号", "外部编号", "商户", "产品", "机器", "通道", 
                                "交易类型", "交易金额", "手续费", "费率", "分润金额", "状态", "交易时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (Transaction t : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(t.getTransNo() != null ? t.getTransNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(t.getOutTransNo() != null ? t.getOutTransNo() : "");

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(t.getMerchantName() != null ? t.getMerchantName() : "");

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(t.getProductName() != null ? t.getProductName() : "");

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(t.getMachineNo() != null ? t.getMachineNo() : "");

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(t.getChannelName() != null ? t.getChannelName() : "");

                Cell cell6 = row.createCell(6);
                String transTypeText = "";
                if (t.getTransType() != null) {
                    switch (t.getTransType()) {
                        case 1: transTypeText = "消费"; break;
                        case 2: transTypeText = "撤销"; break;
                        case 3: transTypeText = "退款"; break;
                        default: transTypeText = "其他";
                    }
                }
                cell6.setCellValue(transTypeText);
                cell6.setCellStyle(centerStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(t.getTransAmount() != null ? t.getTransAmount().doubleValue() : 0);
                cell7.setCellStyle(moneyStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(t.getFeeAmount() != null ? t.getFeeAmount().doubleValue() : 0);
                cell8.setCellStyle(moneyStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(t.getRate() != null ? t.getRate().toString() : "");
                cell9.setCellStyle(centerStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(t.getProfitAmount() != null ? t.getProfitAmount().doubleValue() : 0);
                cell10.setCellStyle(moneyStyle);

                Cell cell11 = row.createCell(11);
                String statusText = "";
                if (t.getStatus() != null) {
                    switch (t.getStatus()) {
                        case 0: statusText = "处理中"; break;
                        case 1: statusText = "成功"; break;
                        case 2: statusText = "失败"; break;
                        case 3: statusText = "已撤销"; break;
                        default: statusText = "未知";
                    }
                }
                cell11.setCellValue(statusText);
                cell11.setCellStyle(centerStyle);

                Cell cell12 = row.createCell(12);
                cell12.setCellValue(t.getTransTime() != null ? t.getTransTime().toString().replace("T", " ") : "");
                cell12.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("交易明细列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出交易明细Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }
}
