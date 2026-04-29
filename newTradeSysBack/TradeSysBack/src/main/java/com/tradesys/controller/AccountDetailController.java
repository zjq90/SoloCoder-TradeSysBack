package com.tradesys.controller;

import com.tradesys.common.PageQuery;
import com.tradesys.common.Result;
import com.tradesys.entity.AccountDetail;
import com.tradesys.service.AccountDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account-detail")
@RequiredArgsConstructor
public class AccountDetailController {

    private final AccountDetailService accountDetailService;

    @GetMapping("/api/list")
    public Result<List<AccountDetail>> list(PageQuery pageQuery, AccountDetail detail,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return accountDetailService.getPage(pageQuery, detail, startDate, endDate);
    }

    @GetMapping("/api/{id}")
    public Result<AccountDetail> getById(@PathVariable Long id) {
        return accountDetailService.getDetail(id);
    }

    @GetMapping("/api/stats")
    public Result<List<Map<String, Object>>> stats(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return accountDetailService.getStatsByAgent(agentId, startDate, endDate);
    }

    @GetMapping("/api/export")
    public void export(AccountDetail detail,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response) {
        accountDetailService.exportExcel(detail, startDate, endDate, response);
    }
}
