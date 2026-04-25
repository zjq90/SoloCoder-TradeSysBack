package com.tradesys.controller;

import com.tradesys.common.Result;
import com.tradesys.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/api/dashboard/overview")
    @ResponseBody
    public Result<Map<String, Object>> getOverview() {
        return dashboardService.getOverview();
    }

    @GetMapping("/api/dashboard/trend")
    @ResponseBody
    public Result<Map<String, Object>> getTrend() {
        return dashboardService.getMonthlyTrend();
    }
}
