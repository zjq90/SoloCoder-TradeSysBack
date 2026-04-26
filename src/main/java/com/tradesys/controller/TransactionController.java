package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.Transaction;
import com.tradesys.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/api/list")
    public Result<List<Transaction>> list(PageQuery pageQuery, Transaction transaction,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return transactionService.getPage(pageQuery, transaction, startDate, endDate);
    }

    @GetMapping("/api/{id}")
    public Result<Transaction> getById(@PathVariable Long id) {
        return transactionService.getDetail(id);
    }

    @GetMapping("/api/daily-stats")
    public Result<List<Map<String, Object>>> dailyStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return transactionService.getDailyStats(startDate, endDate);
    }

    @GetMapping("/api/overview")
    public Result<Map<String, Object>> overview() {
        return transactionService.getOverviewStats();
    }

    @GetMapping("/api/export")
    public void export(Transaction transaction,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response) {
        transactionService.exportExcel(transaction, startDate, endDate, response);
    }
}
