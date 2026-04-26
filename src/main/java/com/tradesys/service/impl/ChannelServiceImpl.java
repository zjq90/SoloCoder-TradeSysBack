package com.tradesys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Channel;
import com.tradesys.entity.ChannelDailyStat;
import com.tradesys.entity.Transaction;
import com.tradesys.mapper.ChannelDailyStatMapper;
import com.tradesys.mapper.ChannelMapper;
import com.tradesys.mapper.TransactionMapper;
import com.tradesys.service.ChannelService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {

    private final ChannelDailyStatMapper channelDailyStatMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public Result<List<Channel>> getPage(PageQuery pageQuery, Channel channel) {
        Page<Channel> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());

        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(channel.getChannelName())) {
            wrapper.like(Channel::getChannelName, channel.getChannelName());
        }
        if (StrUtil.isNotBlank(channel.getChannelNo())) {
            wrapper.eq(Channel::getChannelNo, channel.getChannelNo());
        }
        if (channel.getChannelType() != null) {
            wrapper.eq(Channel::getChannelType, channel.getChannelType());
        }
        if (channel.getStatus() != null) {
            wrapper.eq(Channel::getStatus, channel.getStatus());
        }
        wrapper.orderByDesc(Channel::getCreateTime);

        Page<Channel> resultPage = this.page(page, wrapper);

        LocalDate today = LocalDate.now();
        for (Channel c : resultPage.getRecords()) {
            ChannelDailyStat stat = channelDailyStatMapper.selectByChannelAndDate(c.getId(), today);
            if (stat != null) {
                c.setTodayAmount(stat.getTotalAmount());
                c.setTodayCount(stat.getTotalCount());
                if (c.getDailyLimit() != null && c.getDailyLimit().compareTo(BigDecimal.ZERO) > 0) {
                    c.setRemainingAmount(c.getDailyLimit().subtract(stat.getTotalAmount()));
                }
            } else {
                c.setTodayAmount(BigDecimal.ZERO);
                c.setTodayCount(0);
                c.setRemainingAmount(c.getDailyLimit());
            }
        }

        return Result.success(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public Result<Channel> getDetail(Long id) {
        Channel channel = baseMapper.selectDetailById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        return Result.success(channel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addChannel(Channel channel) {
        if (StrUtil.isBlank(channel.getChannelNo())) {
            channel.setChannelNo(generateChannelNo());
        } else {
            Channel exist = baseMapper.selectOne(
                new LambdaQueryWrapper<Channel>().eq(Channel::getChannelNo, channel.getChannelNo())
            );
            if (exist != null) {
                return Result.error("通道编号已存在");
            }
        }

        if (channel.getStatus() == null) {
            channel.setStatus(1);
        }

        this.save(channel);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateChannel(Channel channel) {
        Channel existChannel = this.getById(channel.getId());
        if (existChannel == null) {
            return Result.error("通道不存在");
        }

        this.updateById(channel);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteChannel(Long id) {
        Channel channel = this.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }

        Long transCount = transactionMapper.selectCount(
            new LambdaQueryWrapper<Transaction>().eq(Transaction::getChannelId, id)
        );
        if (transCount > 0) {
            return Result.error("通道下存在交易记录，无法删除");
        }

        this.removeById(id);

        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateStatus(Long id, Integer status) {
        Channel channel = this.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        channel.setStatus(status);
        this.updateById(channel);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateThreshold(Long id, BigDecimal thresholdPercent) {
        Channel channel = this.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        if (thresholdPercent != null) {
            if (thresholdPercent.compareTo(BigDecimal.ZERO) < 0 || thresholdPercent.compareTo(new BigDecimal("100")) > 0) {
                return Result.error("阈值百分比必须在0-100之间");
            }
            channel.setThresholdPercent(thresholdPercent);
            this.updateById(channel);
        }
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDailyLimit(Long id, BigDecimal dailyLimit) {
        Channel channel = this.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        if (dailyLimit.compareTo(BigDecimal.ZERO) < 0) {
            return Result.error("日限额不能为负数");
        }
        channel.setDailyLimit(dailyLimit);
        this.updateById(channel);
        return Result.ok();
    }

    @Override
    public Result<List<Map<String, Object>>> getChannelOptions() {
        List<Channel> channels = this.list(
            new LambdaQueryWrapper<Channel>()
                .eq(Channel::getStatus, 1)
                .orderByAsc(Channel::getChannelNo)
        );

        List<Map<String, Object>> options = new ArrayList<>();
        for (Channel channel : channels) {
            Map<String, Object> option = new HashMap<>();
            option.put("id", channel.getId());
            option.put("channelNo", channel.getChannelNo());
            option.put("channelName", channel.getChannelName());
            option.put("channelType", channel.getChannelType());
            option.put("rate", channel.getRate());
            option.put("dailyLimit", channel.getDailyLimit());
            options.add(option);
        }

        return Result.success(options);
    }

    @Override
    public void exportExcel(Channel channel, HttpServletResponse response) {
        LambdaQueryWrapper<Channel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(channel.getChannelName())) {
            wrapper.like(Channel::getChannelName, channel.getChannelName());
        }
        if (StrUtil.isNotBlank(channel.getChannelNo())) {
            wrapper.eq(Channel::getChannelNo, channel.getChannelNo());
        }
        if (channel.getChannelType() != null) {
            wrapper.eq(Channel::getChannelType, channel.getChannelType());
        }
        if (channel.getStatus() != null) {
            wrapper.eq(Channel::getStatus, channel.getStatus());
        }
        wrapper.orderByDesc(Channel::getCreateTime);

        List<Channel> list = this.list(wrapper);

        LocalDate today = LocalDate.now();
        for (Channel c : list) {
            ChannelDailyStat stat = channelDailyStatMapper.selectByChannelAndDate(c.getId(), today);
            if (stat != null) {
                c.setTodayAmount(stat.getTotalAmount());
                c.setTodayCount(stat.getTotalCount());
            } else {
                c.setTodayAmount(BigDecimal.ZERO);
                c.setTodayCount(0);
            }
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("通道管理");

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

            String[] headers = {"通道编号", "通道名称", "通道类型", "服务商", "费率", "单日限额", 
                                "单笔限额", "商户限额", "今日交易额", "今日笔数", "状态", "创建时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 15 * 256);
            }

            int rowNum = 1;
            for (Channel c : list) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(c.getChannelNo() != null ? c.getChannelNo() : "");
                cell0.setCellStyle(centerStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(c.getChannelName() != null ? c.getChannelName() : "");

                Cell cell2 = row.createCell(2);
                String typeText = "";
                if (c.getChannelType() != null) {
                    switch (c.getChannelType()) {
                        case 1: typeText = "微信支付"; break;
                        case 2: typeText = "支付宝"; break;
                        case 3: typeText = "银联"; break;
                        case 4: typeText = "其他"; break;
                        default: typeText = "未知";
                    }
                }
                cell2.setCellValue(typeText);
                cell2.setCellStyle(centerStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(c.getProvider() != null ? c.getProvider() : "");

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(c.getRate() != null ? c.getRate().toString() : "");
                cell4.setCellStyle(centerStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(c.getDailyLimit() != null ? c.getDailyLimit().doubleValue() : 0);
                cell5.setCellStyle(moneyStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(c.getSingleLimit() != null ? c.getSingleLimit().doubleValue() : 0);
                cell6.setCellStyle(moneyStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(c.getMerchantLimit() != null ? c.getMerchantLimit().doubleValue() : 0);
                cell7.setCellStyle(moneyStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(c.getTodayAmount() != null ? c.getTodayAmount().doubleValue() : 0);
                cell8.setCellStyle(moneyStyle);

                Cell cell9 = row.createCell(9);
                cell9.setCellValue(c.getTodayCount() != null ? c.getTodayCount() : 0);
                cell9.setCellStyle(centerStyle);

                Cell cell10 = row.createCell(10);
                cell10.setCellValue(c.getStatus() != null && c.getStatus() == 1 ? "启用" : "禁用");
                cell10.setCellStyle(centerStyle);

                Cell cell11 = row.createCell(11);
                cell11.setCellValue(c.getCreateTime() != null ? c.getCreateTime().toString().replace("T", " ") : "");
                cell11.setCellStyle(centerStyle);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("通道管理列表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出通道管理Excel失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    private String generateChannelNo() {
        return "CH" + IdUtil.getSnowflake(1, 1).nextIdStr();
    }
}
