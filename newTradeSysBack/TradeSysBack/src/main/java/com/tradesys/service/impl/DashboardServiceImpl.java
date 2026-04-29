package com.tradesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tradesys.common.Result;
import com.tradesys.entity.*;
import com.tradesys.mapper.*;
import com.tradesys.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AgentMapper agentMapper;
    private final MerchantMapper merchantMapper;
    private final TransactionMapper transactionMapper;
    private final ProfitShareMapper profitShareMapper;
    private final MachineMapper machineMapper;

    @Override
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        LocalDateTime yesterdayStart = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
        LocalDateTime yesterdayEnd = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

        // 代理统计
        long totalAgents = agentMapper.selectCount(null);
        long activeAgents = agentMapper.selectCount(new LambdaQueryWrapper<Agent>().eq(Agent::getStatus, 1));

        // 商户统计
        long totalMerchants = merchantMapper.selectCount(null);
        long activeMerchants = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, 1));

        // 今日交易
        BigDecimal todayAmount = transactionMapper.sumAmountByTimeRange(todayStart, todayEnd);
        long todayCount = transactionMapper.countByTimeRange(todayStart, todayEnd);

        // 昨日交易
        BigDecimal yesterdayAmount = transactionMapper.sumAmountByTimeRange(yesterdayStart, yesterdayEnd);
        long yesterdayCount = transactionMapper.countByTimeRange(yesterdayStart, yesterdayEnd);

        // 今日分润
        BigDecimal todayProfit = profitShareMapper.sumByTimeRange(todayStart, todayEnd);

        // 机器统计
        long totalMachines = machineMapper.selectCount(null);
        long boundMachines = machineMapper.countByStatus(2);

        result.put("totalAgents", totalAgents);
        result.put("activeAgents", activeAgents);
        result.put("totalMerchants", totalMerchants);
        result.put("activeMerchants", activeMerchants);
        result.put("todayAmount", todayAmount != null ? todayAmount : BigDecimal.ZERO);
        result.put("todayCount", todayCount);
        result.put("yesterdayAmount", yesterdayAmount != null ? yesterdayAmount : BigDecimal.ZERO);
        result.put("yesterdayCount", yesterdayCount);
        result.put("todayProfit", todayProfit != null ? todayProfit : BigDecimal.ZERO);
        result.put("totalMachines", totalMachines);
        result.put("boundMachines", boundMachines);

        return Result.success(result);
    }

    @Override
    public Result<Map<String, Object>> getMonthlyTrend() {
        Map<String, Object> result = new HashMap<>();

        // 获取近30天的日期
        List<String> dates = new ArrayList<>();
        List<BigDecimal> transactionAmounts = new ArrayList<>();
        List<Long> merchantCounts = new ArrayList<>();
        List<Long> agentCounts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 模拟近30天的数据
        Random random = new Random();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            // 交易金额 (随机波动)
            BigDecimal baseAmount = new BigDecimal("500000");
            BigDecimal fluctuation = new BigDecimal(random.nextInt(200000) - 100000);
            transactionAmounts.add(baseAmount.add(fluctuation).max(BigDecimal.ZERO));

            // 商户新增 (随机)
            merchantCounts.add((long) (random.nextInt(20) + 5));

            // 代理新增 (随机)
            agentCounts.add((long) (random.nextInt(5) + 1));
        }

        result.put("dates", dates);
        result.put("transactionAmounts", transactionAmounts);
        result.put("merchantCounts", merchantCounts);
        result.put("agentCounts", agentCounts);

        return Result.success(result);
    }
}
