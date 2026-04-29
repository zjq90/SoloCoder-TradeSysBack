package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.ProfitShare;
import com.tradesys.service.ProfitShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profit")
@RequiredArgsConstructor
public class ProfitShareController {

    private final ProfitShareService profitShareService;

    @GetMapping("/api/list")
    public Result<List<ProfitShare>> list(PageQuery pageQuery, ProfitShare profit,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return profitShareService.getPage(pageQuery, profit, startDate, endDate);
    }

    @GetMapping("/api/{id}")
    public Result<ProfitShare> getById(@PathVariable Long id) {
        return profitShareService.getDetail(id);
    }

    @PostMapping("/api/settle/{id}")
    public Result<Void> settle(@PathVariable Long id) {
        return profitShareService.settleProfit(id);
    }

    @PostMapping("/api/batch-settle")
    public Result<Void> batchSettle(@RequestBody List<Long> ids) {
        return profitShareService.batchSettle(ids);
    }

    @GetMapping("/api/stats")
    public Result<List<Map<String, Object>>> stats(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return profitShareService.getStatsByAgent(agentId, startDate, endDate);
    }

    @GetMapping("/api/export")
    public void export(ProfitShare profit,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response) {
        profitShareService.exportExcel(profit, startDate, endDate, response);
    }
}
