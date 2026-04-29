package com.tradesys.service;

import com.tradesys.common.Result;

import java.util.Map;

public interface DashboardService {

    Result<Map<String, Object>> getOverview();

    Result<Map<String, Object>> getMonthlyTrend();
}
